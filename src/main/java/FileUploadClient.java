import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
/**
 * 文件传送
 * 发送文件流到服务器端
 * 服务器端使用SpringBoot的MultipartFile接收
 *
 * 适用场景：
 * 绝对路径的URL文件，不存储到本地，转换成stream,直接使用HTTPClient传送到SpringBoot
**/

public class FileUploadClient {

    public static void main(String[] args) throws Exception {

        //接收文件的服务器地址
        String url = "http://127.0.0.1:8090/springbootdemo/log/upload";
        String pathname = new File("logs" + File.separator + "log_20190310.log").getCanonicalPath();
        logUpload(url, pathname);
        System.out.println("Hello World!");
    }


    private static void logUpload(String url, String pathname) {
        //文件URL，此处取豆瓣上的一个图片
        String fileUrl ="https://img1.doubanio.com/view/photo/l/public/p2537149328.webp";
        try {
            //提取到文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
            //转换成文件流
            InputStream is = new URL(fileUrl).openStream();

            //创建HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            /*绑定文件参数，传入文件流和contenttype，此处也可以继续添加其他formdata参数*/
            builder.addBinaryBody("file", is, ContentType.MULTIPART_FORM_DATA,fileName);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            //执行提交
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if(responseEntity != null) {
                //将响应的内容转换成字符串
                String result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));

                //此处根据服务器返回的参数转换，这里返回的是JSON格式
                JSONObject output = JSON.parseObject(result);
                JSONArray body = output.getJSONArray("body");
                String resUrl = body.get(0) + "";

                System.out.println(resUrl);
            }
            if(is != null) {
               is.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
