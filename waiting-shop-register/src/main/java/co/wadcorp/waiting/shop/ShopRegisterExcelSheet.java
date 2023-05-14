package co.wadcorp.waiting.shop;

import co.wadcorp.waiting.PosRegisterRequest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ShopRegisterExcelSheet {

  private static final int FIRST_ROW = 1;

  private final Sheet sheet;

  public ShopRegisterExcelSheet(Sheet sheet) {
    this.sheet = sheet;
  }

  public int getDataLength() {
    return this.sheet.getPhysicalNumberOfRows() - FIRST_ROW;
  }

  public PosRegisterRequest getRowData(int rowIndex) {
    Row row = sheet.getRow(rowIndex);

    ShopRegisterExcelRow shopRegisterExcelRow = new ShopRegisterExcelRow(row);

    return shopRegisterExcelRow.getExcelData();
  }
}
