package com.lafresh.kiosk.shoppingcart;

public class ShoppingCartManager {
    private static ShoppingCartManager instance;

    private int catePosition;

    public static ShoppingCartManager getInstance() {
        if (instance == null) {
            instance = new ShoppingCartManager();
        }
        return instance;
    }

    public int getCatePosition() {
        return catePosition;
    }

    public void setCatePosition(int catePosition) {
        this.catePosition = catePosition;
    }
}
