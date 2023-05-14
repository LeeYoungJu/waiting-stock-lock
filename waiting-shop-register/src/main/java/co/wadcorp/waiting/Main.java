package co.wadcorp.waiting;

import co.wadcorp.waiting.shop.ShopRegisterExcelSheet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

  private static final int FIRST_SHEET = 0;

  public static void main(String[] args) throws Exception {

    RestTemplateClient client = new RestTemplateClient();

    URL resource = Main.class.getClassLoader().getResource("shop_register.xlsx");
    ObjectMapper mapper = new ObjectMapper();

    File reader = new File(resource.getFile());
    Workbook workbook = getWorkbook(reader);

    var worksheet = new ShopRegisterExcelSheet(workbook.getSheetAt(FIRST_SHEET));

    for (int i = 1; i <= worksheet.getDataLength(); i++) {
      PosRegisterRequest request = worksheet.getRowData(i);
      String result = client.post("https://admin-api.catchpos.co.kr/admin/v1/shop/register",
          mapper.writeValueAsString(request));
      System.out.println(result);
      Thread.sleep(1000);
    }
  }

  private static Workbook getWorkbook(File file) throws IOException {
    var extension = FilenameUtils.getExtension(file.getName());

    if ("xlsx".equals(extension)) {
      return new XSSFWorkbook(new FileInputStream(file));
    }

    if ("xls".equals(extension)) {
      return new HSSFWorkbook(new FileInputStream(file));
    }

    throw new RuntimeException("엑셀 파일 확장자가 올바르지 않습니다. xlsx 또는 xls 확장자 파일을 사용해주세요.");
  }

  public static class RestTemplateClient {

    public static final MediaType JSON
        = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    public String post(String url, String json) throws IOException {
      RequestBody body = RequestBody.create(json, JSON);
      Request request = new Request.Builder()
          .url(url)
          .post(body)
          .build();
      try (Response response = client.newCall(request).execute()) {
        return response.body().string();
      }
    }

  }


}