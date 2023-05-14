package co.wadcorp.waiting.shop;

import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.BIZ_ADDRESS;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.BIZ_NUM;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.BIZ_PHONE;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.BIZ_PRESIDENT_NAME;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.BIZ_SHOP_NAME;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.EMAIL;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.IS_CASH_MANAGER;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.IS_CATCH_POS;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.IS_CATCH_WAITING;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.PHONE;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.POS_MODE;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.SHOP_NAME;
import static co.wadcorp.waiting.shop.ShopRegisterExcelCellConstant.USER_PW;

import co.wadcorp.waiting.PosRegisterRequest;
import co.wadcorp.waiting.PosRegisterRequest.BusinessInfo;
import co.wadcorp.waiting.PosRegisterRequest.ShopInfo;
import co.wadcorp.waiting.PosRegisterRequest.UserInfo;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ShopRegisterExcelRow {

    private static final int ZERO = 0;
    private final Row row;

    public ShopRegisterExcelRow(Row row) {
        this.row = row;
    }

    public int getNumericCellValue(int index) {
        Cell cell = this.row.getCell(index);
        if(Objects.isNull(cell)) {
            return ZERO;
        }

        if(cell.getCellType() == CellType.STRING) {
            return Integer.parseInt(cell.getStringCellValue());
        }

        return (int) cell.getNumericCellValue();
    }

    public String getStringCellValue(int index) {
        Cell cell = this.row.getCell(index);
        if(Objects.isNull(cell)) {
            return "";
        }

        if(cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }

        return cell.getStringCellValue();
    }

    public Boolean getBooleanCellValue(int index) {
        Cell cell = this.row.getCell(index);
        if(Objects.isNull(cell)) {
            return false;
        }

        if(cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        return false;
    }

    public PosRegisterRequest getExcelData() {
        return PosRegisterRequest.builder()
            .userInfo(UserInfo.builder()
                .email(this.getStringCellValue(EMAIL.index()))
                .userPw(this.getStringCellValue(USER_PW.index()))
                .phone(this.getStringCellValue(PHONE.index()))
                .build())
            .shopInfo(
                ShopInfo.builder()
                    .shopName(this.getStringCellValue(SHOP_NAME.index()))
                    .isCatchPos(this.getBooleanCellValue(IS_CATCH_POS.index()))
                    .isCatchWaiting(this.getBooleanCellValue(IS_CATCH_WAITING.index()))
                    .posMode(this.getStringCellValue(POS_MODE.index()))
                    .isCashManagement(this.getBooleanCellValue(IS_CASH_MANAGER.index()))
                    .build()
            )
            .businessInfo(
                BusinessInfo.builder()
                    .bizShopName(this.getStringCellValue(BIZ_SHOP_NAME.index()))
                    .bizNum(this.getStringCellValue(BIZ_NUM.index()))
                    .bizAddress(this.getStringCellValue(BIZ_ADDRESS.index()))
                    .bizPresidentName(this.getStringCellValue(BIZ_PRESIDENT_NAME.index()))
                    .bizPhone(this.getStringCellValue(BIZ_PHONE.index()))
                    .build()
            )


            .build();
    }
}
