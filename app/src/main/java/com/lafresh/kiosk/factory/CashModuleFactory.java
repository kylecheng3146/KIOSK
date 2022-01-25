package com.lafresh.kiosk.factory;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 2021/4/26.
 */

public class CashModuleFactory {

    public CashModuleFactory() {

    }

    public enum DevicePort {
        ONE_DOLLAR("MiniHooper","-1.3.3.2"),
        FIVE_DOLLAR("MiniHooper","/dev/ttyCOM4"),
        TEN_DOLLAR("MiniHooper","-1.4.3.1"),
        FIFTY_DOLLAR("MiniHooper","-1.4.3.2"),
        GIVE_PAPER_MONEY("NED1000","/dev/ttyCOM2"),
        GET_DOLLAR("UCAx","/dev/ttyCOM5"),
        GET_PAPER_MONEY("ICT104","/dev/ttyCOM3");
        private final String port;
        private final String name;
        DevicePort(String name,String port) {
            this.port = port;
            this.name = name;
        }
        public String getPort() {
            return port;
        }
        public String getName() {
            return name;
        }
    }

    public enum Commend {
        REGISTER_CALLBACK("RegisterCallback"),
        UN_REGISTER_CALLBACK("UnregisterCallback"),
        TRANSACTION("Transaction"),
        CANCEL_TRANSACTION("CancelTransaction"),
        ADD_MONEY("AddMoney"),
        OUT_ALL_MONEY("OutAllMoney"),
        OUTPUT_MONEY("OutputMoney"),
        ENUMRATE_TTY_DEVICES("EnumrateTTYDevices"),
        TEST_ONE_DEVICE("TestOneDevice"),
        PORT_SETTING("PortSetting"),
        GET_DOLLAR(""),
        GET_PAPER_MONEY("");
        private final String commend;
        Commend(String commend) {
            this.commend = commend;
        }
        public String getCommend() {
            return commend;
        }
    }

    @NotNull
    public JSONObject registerCallBack() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.REGISTER_CALLBACK.getCommend());
            obj.put("TaskID", "1");
            obj.put("IP", "127.0.0.1");
            obj.put("Port", 8300);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
    @NotNull
    public JSONObject unRegisterCallBack() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.UN_REGISTER_CALLBACK.getCommend());
            obj.put("TaskID", "1");
            obj.put("IP", "127.0.0.1");
            obj.put("Port", 8300);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject transaction(String taskID,int amount) {
        JSONObject obj = new JSONObject();
        List<Integer> list = new ArrayList<>();
        list.add(500);
        list.add(100);
        list.add(50);
        list.add(10);
        list.add(5);
        JSONArray array = new JSONArray();
        for(int i = 0; i < list.size(); i++) {
            array.put(list.get(i));
        }
        try {
            obj.put("Command", Commend.TRANSACTION.getCommend());
            obj.put("TaskID", taskID);
            obj.put("Timeout", 120);
            obj.put("AcceptCoinTypes", array);
            obj.put("Amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject cancelTransaction(String taskID) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.CANCEL_TRANSACTION.getCommend());
            obj.put("TaskID", "5");
            obj.put("CancelTaskID", taskID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject addMoney(JSONArray array) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.ADD_MONEY.getCommend());
            obj.put("TaskID", "3");
            obj.put("Box", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject addMoneyTest(String port,int quantity) {
        JSONObject obj = new JSONObject();
        JSONObject box = new JSONObject();
        try {
            box.put("Name",port);
            box.put("ID","00");
            box.put("Quantity",quantity);
            JSONArray array = new JSONArray();
            array.put(box);
            obj.put("Command", Commend.ADD_MONEY.getCommend());
            obj.put("TaskID", "3");
            obj.put("Box", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject outAllMoney(String taskId,String port) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.OUT_ALL_MONEY.getCommend());
            obj.put("TaskID", taskId);
            obj.put("Name", port);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject outputMoney(String taskId,int amount) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.OUTPUT_MONEY.getCommend());
            obj.put("TaskID", taskId);
            obj.put("Amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject enumrateTTYDevices() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.ENUMRATE_TTY_DEVICES.getCommend());
            obj.put("TaskID", "5");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject testOneDevice() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", Commend.TEST_ONE_DEVICE.getCommend());
            obj.put("TaskID", "2");
            obj.put("Name", "/dev/ttyCOM1");
            obj.put("ModelName", "UCAx");
            obj.put("Identifier", "00");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject portSetting() {
        JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            array.put(oneDollarPortSetting());
            array.put(fiveDollarPortSetting());
            array.put(tenDollarPortSetting());
            array.put(fiftyDollarPortSetting());
            array.put(getDollarPortSetting());
            array.put(givePaperMoneyPortSetting());
            array.put(getPaperMoneyPortSetting());
            obj.put("Command", Commend.PORT_SETTING.getCommend());
            obj.put("TaskID", "6");
            obj.put("ExecutionMode", 2);
            obj.put("Mode1Timeout", 120);
            obj.put("Detail", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject oneDollarPortSetting() throws JSONException {
        JSONObject box = new JSONObject();
        box.put("Name", DevicePort.ONE_DOLLAR.getPort());
        box.put("CoinMachine",1);
        box.put("InputMachine",1);
        box.put("ModelName", DevicePort.FIFTY_DOLLAR.getName());
        box.put("Identifier","0");
        box.put("CoinType",1);
        return box;
    }

    @NotNull
    public JSONObject fiveDollarPortSetting() throws JSONException {
        JSONObject box = new JSONObject();
        box.put("Name", DevicePort.FIVE_DOLLAR.getPort());
        box.put("CoinMachine",1);
        box.put("InputMachine",1);
        box.put("ModelName", DevicePort.FIFTY_DOLLAR.getName());
        box.put("Identifier","0");
        box.put("CoinType",5);
        return box;
    }

    @NotNull
    public JSONObject tenDollarPortSetting() throws JSONException {
        JSONObject box = new JSONObject();
        box.put("Name", DevicePort.TEN_DOLLAR.getPort());
        box.put("CoinMachine",1);
        box.put("InputMachine",1);
        box.put("ModelName", DevicePort.FIFTY_DOLLAR.getName());
        box.put("Identifier","0");
        box.put("CoinType",10);
        return box;
    }

    @NotNull
    public JSONObject fiftyDollarPortSetting() throws JSONException {
        JSONObject box = new JSONObject();
        box.put("Name", DevicePort.FIFTY_DOLLAR.getPort());
        box.put("CoinMachine",1);
        box.put("InputMachine",1);
        box.put("ModelName", DevicePort.FIFTY_DOLLAR.getName());
        box.put("Identifier","0");
        box.put("CoinType",50);
        return box;
    }

    @NotNull
    public JSONObject givePaperMoneyPortSetting() throws JSONException {
        JSONObject box = new JSONObject();
        box.put("Name", DevicePort.GIVE_PAPER_MONEY.getPort());
        box.put("CoinMachine",0);
        box.put("InputMachine",0);
        box.put("ModelName", DevicePort.GIVE_PAPER_MONEY.getName());
        box.put("Identifier","00");
        box.put("CoinType",100);
        return box;
    }

    @NotNull
    public JSONObject getPaperMoneyPortSetting() throws JSONException {
        JSONObject box = new JSONObject();
        box.put("Name", DevicePort.GET_PAPER_MONEY.getPort());
        box.put("CoinMachine",0);
        box.put("InputMachine",1);
        box.put("ModelName", DevicePort.GET_PAPER_MONEY.getName());
        box.put("Identifier","00");
        box.put("CoinType",10);
        return box;
    }

    @NotNull
    public JSONObject getDollarPortSetting() throws JSONException {
        JSONObject box = new JSONObject();
        box.put("Name", DevicePort.GET_DOLLAR.getPort());
        box.put("CoinMachine",1);
        box.put("InputMachine",1);
        box.put("ModelName", DevicePort.GET_DOLLAR.getName());
        box.put("Identifier","00");
        box.put("CoinType",10);
        return box;
    }

    @NotNull
    public JSONObject errorRecovered() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", "ErrorRecovered");
            obj.put("TaskID", "6");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject resetAll() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", "ResetAll");
            obj.put("TaskID", "9");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @NotNull
    public JSONObject getMoney() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Command", "GetMoney");
            obj.put("TaskID", "10");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
