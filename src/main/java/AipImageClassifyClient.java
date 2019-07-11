import com.baidu.aip.imageclassify.AipImageClassify;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AipImageClassifyClient extends AipImageClassify {
    //设置APPID/AK/SK
    public static final String APP_ID = "15719618";
    public static final String API_KEY = "acGpYVX6WK4g8qlxukphHklB";
    public static final String SECRET_KEY = "r7oQ6TYwLDNnZMiyS8XqWcTL8QmV5bGd";

    public static String MAP_AK = "7X2NPnW6dZuXtNxC9Hm50deFVC5yVwfo";

    public static String MAP_URL = "http://api.map.baidu.com/geocoder/v2/?output=json&ak=" + MAP_AK;

    public AipImageClassifyClient(String appId, String apiKey, String secretKey) {
        super(appId, apiKey, secretKey);
    }

    public static List getObject(JSONObject jsonObject) {
        List list = new ArrayList();
        try {
            //JSONObject jsonObject = new JSONObject(jsonString);
            // 返回json的数组
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                list.add(jsonObject2);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return list;
    }

    public static String getAddress(double lng, double lat) {
        String address = "";
        String location = lat + "," + lng;
        BufferedReader in = null;
        URL url = null;
        URLConnection connection = null;
        try {
            url = new URL(MAP_URL + "&location=" + location);
            connection = url.openConnection();
            connection.setDoOutput(true);
            in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            StringBuilder text = new StringBuilder("");
            while ((line = in.readLine()) != null) {
                text.append(line.trim());
            }
            JSONObject result = new JSONObject(text.toString());
            if (result != null && result.getInt("status") == 0) {
                address = result.getJSONObject("result").getString("formatted_address");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static void main(String[] args) throws ImageProcessingException, IOException, MetadataException {
        // 初始化一个AipImageClassifyClient
        /*AipImageClassifyClient client = new AipImageClassifyClient(APP_ID, API_KEY, SECRET_KEY);

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("baike_num", "5");

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        String path = "src/main/resources/test.jpg";
        JSONObject res = client.advancedGeneral(path, options);
        List<JSONObject> list = getObject(res);
        for (JSONObject i : list)
            System.out.print(i.getString("keyword") + " ");*/

        Metadata metadata = ImageMetadataReader.readMetadata(new File("src/main/resources/test5.jpg"));

        for (Directory d : metadata.getDirectories()
        ) {
            for (Tag tag : d.getTags())
                System.out.println(tag);
            //log.info("\n");
        }

        ExifIFD0Directory ifd0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        String cameraModel = ifd0Directory.getString(ExifSubIFDDirectory.TAG_MODEL);

        try {
            Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            int width = directory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
            int height = directory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
            //String cameraModel = directory.getString(ExifSubIFDDirectory.TAG_MODEL);
            System.out.println("-----------------\n" + date.toString());
            System.out.println(height + "*" + width);
            System.out.println(cameraModel);
        } catch (Exception e) {
            //ExifIFD0Directory ifd0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            int width = ifd0Directory.getInt(ExifIFD0Directory.TAG_IMAGE_WIDTH);
            int height = ifd0Directory.getInt(ExifIFD0Directory.TAG_IMAGE_HEIGHT);
            Date date = ifd0Directory.getDate(ExifIFD0Directory.TAG_DATETIME);
            System.out.println("-----------------\n" + date.toString());
            System.out.println(height + "*" + width);
            System.out.println(cameraModel);
        }
        //log.info("拍摄日期：{} ,尺寸：{}x{}", date, width, height);


        try {
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            final GeoLocation geoLocation = gpsDirectory.getGeoLocation();
            double latitude = geoLocation.getLatitude();
            double longitude = geoLocation.getLongitude();
            //log.info("经度{},纬度{}",longitude,latitude);
            System.out.println("-----------------\n" + longitude + "," + latitude);
            System.out.println(getAddress(longitude,latitude));
        } catch (Exception e) {
            System.out.println("-----------------\n" + "无法读取该图片的地理信息");
        }
    }

}
