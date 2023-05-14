package co.wadcorp.waiting.shop;

public enum ShopRegisterExcelCellConstant {
    EMAIL(0),
    USER_PW(1),
    PHONE(2),
    SHOP_NAME(3),
    IS_CATCH_POS(4),
    IS_CATCH_WAITING(5),
    POS_MODE(6),
    IS_CASH_MANAGER(7),
    BIZ_SHOP_NAME(8),
    BIZ_NUM(9),
    BIZ_ADDRESS(10),
    BIZ_PRESIDENT_NAME(11),
    BIZ_PHONE(12);

    private final int index;

    ShopRegisterExcelCellConstant(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
