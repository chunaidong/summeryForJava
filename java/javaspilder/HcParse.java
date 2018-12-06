package com.luban.web;

import com.gavin.devarch.jsoncore.util.CommonUtil;
import com.gavin.devarch.jsoncore.util.DateUtil;
import com.gavin.devarch.jsoncore.util.PropertiesUtil;
import com.luban.util.FileAppend;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *    @author  : wangchun
 *    @time    : 2018/6/5 9:32
 *    desc    : 慧聪网解析
 *    version : v1.0
 * </pre>
 */
public class HcParse {
    private static Logger logger = LogManager.getLogger(HcParse.class);
    //网页驱动
    public WebDriver driverClass;
    //title驱动
    public WebDriver driverTitle;
    //供应商驱动
    public WebDriver driverSupplier;


    public Properties getProperties() throws IOException {
        PropertiesUtil properties = new PropertiesUtil("html-parse.properties");
        return properties.getProperties();
    }

    public String getPropertiesValue(String key) throws IOException{
        return getProperties().getProperty(key);
    }

    /**
     * 解析商品title
     * @param word
     * @return
     * @throws Exception
     */
    public void parseTitle(String word,String path,String percent) throws Exception{
        //是否出错标志
        int countExcption = 0;

        // 设置 chrome 的路径（如果你安装chrome的时候用的默认安装路径，则可省略这步）
        System.setProperty("webdriver.chrome.driver",getPropertiesValue("webdriver.chrome.driver"));
        // 创建一个 Chrome 的浏览器实例
        driverTitle = new ChromeDriver();
        try{
            //慧聪网地址
            driverTitle.get("https://www.hc360.com/");
            // 通过判断 title 内容等待搜索页面加载完毕，间隔秒
            (new WebDriverWait(driverTitle, 5))
                    .until(new ExpectedCondition<WebElement>() {
                        public WebElement apply(WebDriver d) {
                            return d.findElement(By.id("w"));
                        }
                    });
            TimeUnit.SECONDS.sleep(3);
            // 通过 id 找到 搜索框
            WebElement element =driverTitle.findElement(By.id("w"));
            // 输入关键字
            element.sendKeys(word);
            TimeUnit.SECONDS.sleep(1);
            //通过id找到搜索按钮
            WebElement navSearchBtn =driverTitle.findElement(By.id("navSearchBtn"));
            // 提交
            navSearchBtn.click();
            //切换window控制对象
            Object[] handles = driverTitle.getWindowHandles().toArray();
            if (handles.length != 2) {
                System.err.println("window switch err!");
                return;
            }
            driverTitle.switchTo().window((String) handles[1]);
            TimeUnit.SECONDS.sleep(1);
            //采集商品类目信息
            parseClass(word,path,driverTitle);
            /**
             * 采集商品title
             * 1.抓取总页数的30%
             * 2.每个title一行
             */
            //商品title存放集合
            List<String> contents = new ArrayList<String>();
            WebElement content = driverTitle.findElement(By.className("cont-left"));
            //判断是否可以查询到数据
            List<WebElement> newListElement = content.findElements(By.xpath("//div[@class='seaNewList']"));
            if(0 == newListElement.size()){
                logger.info("没有查询到类型为:《《《《《"+word+"》》》》》的数据");
                return;
            }
            //是否有分页数据
            List<WebElement> pageList = content.findElements(By.xpath("//div[@class='s-mod-page']"));
            //处理分页数据
            if(pageList.size()>0){
                //获取角标
                WebElement pageL= content.findElement(By.className("s-mod-page"));
                //获取页数标签
                List<WebElement> a = pageL.findElements(By.tagName("a"));
                //获取商品总页数
                String totalPage = "";
                if(a.size()<=3){
                    totalPage = "1";
                }else {
                    totalPage = a.get(a.size()-2).getText();
                }
                //页数
                int totalArray= Integer.parseInt(totalPage);
                BigDecimal totalZs = new BigDecimal(totalArray);
                BigDecimal jd = new BigDecimal(percent);
                int total = totalZs.multiply(jd).setScale(0,BigDecimal.ROUND_UP).intValue();
                if(total < 3 )total = 3;
                if(total > totalArray) total = totalArray;
                //下拉滚动条到底部
                for(int i = 0 ;i< 5;i++){
                    scrollPage((JavascriptExecutor) driverTitle,i);
                    TimeUnit.SECONDS.sleep(2);
                }
                //标题父组件
                List<WebElement> newName = content.findElements(By.className("newName"));
                //抓取第一页数据
                for(WebElement titleElement : newName){
                    WebElement aElement = titleElement.findElement(By.tagName("a"));
                    if(!aElement.getText().equals("")){
                        contents.add(aElement.getText());
                    }
                }
                //判断total是否大于1,如果大于1 则跳转页面，否则就直接写数据到文件中
                if(total > 1){
                    //跳转到改页面重新抓取数据
                    WebElement page_next =  content.findElement(By.className("s-mod-page"));
                    for (int i = 2 ; i <= total ; i++){
                        scrollToPageControl((JavascriptExecutor) driverTitle);
                        // 通过 id 找到 跳转页面输入框
                        WebElement e =page_next.findElement(By.id("e"));
                        // 输入关键字
                        e.sendKeys(i+"");
                        TimeUnit.SECONDS.sleep(1);
                        //通过id找到搜索按钮
                        WebElement jump_go  =page_next.findElement(By.tagName("button"));
                        // 提交
                        jump_go.click();
                        //下拉滚动条到底部
                        for(int j = 0 ;j< 5;j++){
                            scrollPage((JavascriptExecutor) driverTitle,j);
                            TimeUnit.SECONDS.sleep(2);
                        }
                        WebElement contentPage = driverTitle.findElement(By.className("cont-left"));
                        //标题父组件
                        List<WebElement> newNamePage = contentPage.findElements(By.className("newName"));
                        //抓取第一页数据
                        for(WebElement titleElement : newNamePage){
                            WebElement aElement = titleElement.findElement(By.tagName("a"));
                            if(!aElement.getText().equals("")){
                                contents.add(aElement.getText());
                            }
                        }
                        page_next = contentPage.findElement(By.className("page-next"));
                    }
                }
            }else {
                //不分页数据
                for(int j = 0 ;j< 5;j++){
                    scrollPage((JavascriptExecutor) driverTitle,j);
                    TimeUnit.SECONDS.sleep(2);
                }
                //判断是否有推荐信息
                List<WebElement> seaStrips = content.findElements(By.xpath("//div[@class='seaStrip']"));
                if(seaStrips.size() > 0){
                    //取第一ul标签
                    List<WebElement> ulElement = content.findElements(By.tagName("ul"));
                    for(WebElement ul:ulElement){
                        if(!ul.getAttribute("class").equals("scrollLoading-hook")){
                            //标题父组件
                            List<WebElement> newName = ul.findElements(By.className("newName"));
                            //抓取第一页数据
                            for(WebElement titleElement : newName){
                                WebElement aElement = titleElement.findElement(By.tagName("a"));
                                if(!aElement.getText().equals("")){
                                    contents.add(aElement.getText());
                                }
                            }
                        }
                    }
                }else {
                    //标题父组件
                    List<WebElement> newName = content.findElements(By.className("newName"));
                    //抓取第一页数据
                    for(WebElement titleElement : newName){
                        WebElement aElement = titleElement.findElement(By.tagName("a"));
                        if(!aElement.getText().equals("")){
                            contents.add(aElement.getText());
                        }
                    }
                }
            }
            if(contents.isEmpty()){
                logger.info("没有查询到类型为:《《《《《"+word+"》》》》》的数据");
                return;
            }
            FileAppend.appendLines(path+"\\慧聪网\\商品title\\",word+".txt",contents);
            System.out.println("--------------写入商品title完成---------------");

        }catch (Exception e){
            logger.error(e);
            logger.error("物料名称" + word + "爬取过程中出现错误，详情如下：");
            logger.error(CommonUtil.getExceptionStr(e));
            e.printStackTrace();
        }finally {
            driverTitle.quit();
        }

    }


    public void scrollPage(JavascriptExecutor driver_js, int times) {
        String[] js = {
                "window.scrollTo(0,document.body.scrollHeight-(4*(document.body.scrollHeight/5)));",
                "window.scrollTo(document.body.scrollHeight-(4*(document.body.scrollHeight/5)),document.body.scrollHeight-(3*(document.body.scrollHeight/5)));",
                "window.scrollTo(document.body.scrollHeight-(3*(document.body.scrollHeight/5)),document.body.scrollHeight-(2*(document.body.scrollHeight/5)));",
                "window.scrollTo(document.body.scrollHeight-(2*(document.body.scrollHeight/5)),document.body.scrollHeight-(1*(document.body.scrollHeight/5)));",
                "window.scrollTo(document.body.scrollHeight-(1*(document.body.scrollHeight/5)),document.body.scrollHeight);" };
        driver_js.executeScript(js[times]);
    }

//    public static void main(String[] args) {
    public static void main(String words, String paths, String percents, CountDownLatch begin) {
       final HcParse hcParse = new HcParse();
        //类型/供应商都没有数据
//        final String word = "安瓶FFFFFFFFFF";
        //都是一页数据
        final String word = words;
        final String path = paths;
        final String percent = percents;
        final CountDownLatch end = begin;
//        final String word = "螺纹钢";
//        final String path = "F://";
//        final String percent = "0.03";
        //title 两页 /供应商一页
//        final String word = "架空地线对向下锚底座";
        //title/供应商 100页数据
        // final String word = "钢化玻璃";

//        new Thread(new Runnable() {
//
//            public void run() {
//                try {
//                    hcParse.parseClass(word,path,percent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }finally {
//                    end.countDown();
//                }
//            }
//        }
//        ).start();
        new Thread(new Runnable() {

            public void run() {
                try {
                    hcParse.parseTitle(word,path,percent);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    end.countDown();
                }
            }
        }
        ).start();
        new Thread(new Runnable() {

            public void run() {
                try {
                    hcParse.parseSupplier(word,path,percent);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    end.countDown();
                }
            }
        }
        ).start();


    }

    public void parseSupplier(String word,String path,String percent) throws Exception {


        // 设置 chrome 的路径（如果你安装chrome的时候用的默认安装路径，则可省略这步）
        System.setProperty("webdriver.chrome.driver",getPropertiesValue("webdriver.chrome.driver"));
        // 创建一个 Chrome 的浏览器实例
        driverSupplier = new ChromeDriver();
        //慧聪网地址
        try {
            driverSupplier.get("https://www.hc360.com/");
            // 通过判断 title 内容等待搜索页面加载完毕，间隔秒
            (new WebDriverWait(driverSupplier, 5))
                    .until(new ExpectedCondition<WebElement>() {

                        public WebElement apply(WebDriver d) {
                            return d.findElement(By.id("w"));
                        }
                    });
            // 通过 id 找到 搜索框
            WebElement element =driverSupplier.findElement(By.id("w"));
            // 输入关键字
            element.sendKeys(word);
            TimeUnit.SECONDS.sleep(1);
            //通过id找到搜索按钮
            WebElement navSearchBtn =driverSupplier.findElement(By.id("navSearchBtn"));
            // 提交
            navSearchBtn.click();
            //切换window控制对象
            Object[] handles = driverSupplier.getWindowHandles().toArray();
            if (handles.length != 2) {
                System.err.println("window switch err!");
            }
            driverSupplier.switchTo().window((String) handles[1]);
            TimeUnit.SECONDS.sleep(1);
            //找到表头搜索栏
            WebElement searchElement = driverSupplier.findElement(By.className("tablist-top"));
            //获取公司标签
            WebElement enterprise =searchElement.findElement(By.xpath("//a[contains(text(),'公司')]"));
            //点击公司标签
            enterprise.click();
            TimeUnit.SECONDS.sleep(2);
            //存放集合
            List<String> contentsCompany = new ArrayList<String>();

            //获取商品对象
            WebElement contentCompany = driverSupplier.findElement(By.className("cont-left"));
            //判断是否查询到数据
            List<WebElement> pageList = contentCompany.findElements(By.xpath("//div[@class='s-mod-page']"));
            if(pageList.size()==0){
                logger.info("没有查询到供应商主营产品为:《《《《《"+word+"》》》》》的数据");
                return;
            }
            //获取角标
            WebElement pageCompany = contentCompany.findElement(By.className("s-mod-page"));
            //获取页数标签
            List<WebElement> aLink = pageCompany.findElements(By.tagName("a"));
            //获取商品总页数
            String totalPageCompany = "";
            if(aLink.size()<=3){
                totalPageCompany = "1";
            }else {
                totalPageCompany = aLink.get(aLink.size()-2).getText();
            }

            //页数
            int totalArrayCompany= Integer.parseInt(totalPageCompany);
            BigDecimal totalZsCompany = new BigDecimal(totalArrayCompany);
            BigDecimal jdCompany = new BigDecimal(percent);
            int totalCompany = totalZsCompany.multiply(jdCompany).setScale(0,BigDecimal.ROUND_UP).intValue();
            if(totalCompany < 3 )totalCompany = 3;
            if(totalCompany > totalArrayCompany) totalCompany = totalArrayCompany;
            //下拉滚动条到底部
            for(int i = 0 ;i< 5;i++){
                scrollPage((JavascriptExecutor) driverSupplier,i);
                TimeUnit.SECONDS.sleep(2);
            }
            //标题父组件
            List<WebElement> info = contentCompany.findElements(By.className("info"));
            //抓取第一页数据
            for(WebElement titleElement : info){
                if(!titleElement.getText().equals("")){
                    contentsCompany.add(titleElement.getText());
                }
            }

            //判断total是否大于1,如果大于1 则跳转页面，否则就直接写数据到文件中
            if(totalCompany > 1){
                //跳转到改页面重新抓取数据
                WebElement page_next =  contentCompany.findElement(By.className("page-next"));
                for (int i = 2 ; i <= totalCompany ; i++){
                    scrollToPageControl((JavascriptExecutor) driverSupplier);
                    // 通过 id 找到 跳转页面输入框
                    WebElement e =page_next.findElement(By.id("e"));
                    // 输入关键字
                    e.sendKeys(i+"");
                    TimeUnit.SECONDS.sleep(1);
                    //通过id找到搜索按钮
                    WebElement jump_go  =page_next.findElement(By.tagName("button"));
                    // 提交
                    jump_go.click();
                    //下拉滚动条到底部
                    for(int j = 0 ;j< 5;j++){
                        scrollPage((JavascriptExecutor) driverSupplier,j);
                        TimeUnit.SECONDS.sleep(2);
                    }
                    WebElement contentPage = driverSupplier.findElement(By.className("cont-left"));
                    //标题父组件
                    List<WebElement> newNamePage = contentPage.findElements(By.className("info"));
                    //抓取第一页数据
                    for(WebElement titleElement : newNamePage){
                        if(!titleElement.getText().equals("")){
                            contentsCompany.add(titleElement.getText());
                        }
                    }
                    page_next = contentPage.findElement(By.className("page-next"));
                }
            }
            FileAppend.appendLines(path + "\\慧聪网\\供应商主营产品\\",word+".txt",contentsCompany);
            System.out.println("--------------写入供应商主营产品完成---------------");

        }catch (Exception e){
            logger.info("写入公司数据时出现错误");
            logger.error(e);
        }finally {
            driverSupplier.quit();
        }

    }




    /**
     * 解析类目信息
     * @param
     * @param word
     * @param path
     * @param driverClass
     */
    public void parseClass(String word, String path, WebDriver driverClass) throws Exception {

            //获取类目对象
            WebElement search_list_mod = driverClass.findElement(By.className("search_list_mod"));
            //找到多选按钮
            List<WebElement> moreBtnElements = search_list_mod.findElements(By.xpath("//a[@class='filter_more']"));
            for (WebElement moreBtnElement : moreBtnElements){
                moreBtnElement.click();
            }
            //获取类目下具体信息
            List<WebElement> list_layout = search_list_mod.findElements(By.className("list_layout_title"));
            List<WebElement> list_search_layout = search_list_mod.findElements(By.xpath("//div[@class='list_layout_filter']"));
            StringBuffer sblm=new StringBuffer();
            for(int i = 0;i<list_layout.size();i++){
                String title = "";
                sblm.append(list_layout.get(i).getText());
                List<WebElement> ul =list_search_layout.get(i).findElements(By.tagName("a"));
                for (WebElement li : ul) {
                    if(!li.getText().equals("") && !li.getText().equals("多选") && !li.getText().equals("隐藏") ){
                        title+=li.getText()+"@#@";
                    }
                }
                //去除最后符号
                sblm.append(title.substring(0,title.length()-3));
                //换行
                sblm.append("\r\n");
            }
            //写入文件
            FileAppend.append(path+"\\慧聪网\\类目信息\\",word+".txt", sblm.toString());
            System.out.println("--------------写入类目信息完成---------------");


    }
    public void scrollToPageControl(JavascriptExecutor driver_js) {
        driver_js.executeScript("window.scrollTo(0,document.body.scrollHeight-(1*(document.body.scrollHeight/7)));");
    }

}

