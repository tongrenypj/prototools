import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

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

        //post方式上传文件, 服务器地址 http://10.94.29.20:8989/file/upload
        String url = args[0];
        String fileName = args[1];

        File directory = new File("");//参数为空
        //String courseFile = directory.getCanonicalPath();
        //System.out.println(courseFile);


        File file = new File(directory + fileName);
        fileUpload(url, file);
    }


    private static void fileUpload(String url, File file) {

        try {
            //需要上传的文件
            //创建HttpClient
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            //关键代码：将java.io.File对象添加到HttpEntity（org.apache.http.HttpEntity）对象中：
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file);
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            //执行提交
            HttpResponse response = httpClient.execute(httpPost);
            //获取响应
            HttpEntity responseEntity = response.getEntity();

            int statusCode=response.getStatusLine().getStatusCode();
            if(statusCode== HttpStatus.SC_OK){
                //得到客户段响应的实体内容
                HttpEntity responseHttpEntity = response.getEntity();
                //得到输入流
                InputStream in = responseHttpEntity.getContent();
                //得到输入流的内容
                InputStreamReader isr =new InputStreamReader(in,"utf-8");
                BufferedReader br =new BufferedReader(isr);
                try {
                    while ((br.read())!=-1) {
                        System.out.println(br.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            httpClient.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
