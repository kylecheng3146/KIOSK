package com.lafresh.kiosk.easycard;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andy on 2016/12/30.
 */

public class TxnPar {
    public static int[][] srTnxPar = {
            //	lnProcessCode	 inTagName1  inTagName2  inTagName3  inTagName4  inTagName5  inTagName6  inTagName7  inTagName8  inTagName9  inTagName10 inTagName11 inTagName12 inTagName13 inTagName14 inTagName15 inTagName16 inTagName17 inTagName18 inTagName19 inTagName20

            {216000, 100, 300, 1100, 1101, 1200, 1300, 5501, 5593, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {296000, 100, 300, 400, 1100, 1101, 1200, 1300, 4108, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {390061, 100, 300, 1100, 1101, 1200, 1300, 4108, 4830, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {390861, 100, 300, 1100, 1101, 1200, 1300, 4108, 4830, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {606100, 100, 300, 400, 1100, 1101, 1200, 1300, 4108, 4830, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {620061, 100, 300, 400, 1100, 1101, 1200, 1300, 4830, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {801061, 100, 300, 400, 1100, 1101, 1200, 1300, 4108, 4830, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {816100, 100, 300, 400, 1100, 1101, 1200, 1300, 4108, 4830, 5501, 5581, 5582, 5583, 0, 0, 0, 0, 0, 0, 0},
            {825799, 100, 300, 409, 1100, 1101, 1200, 1300, 4108, 4830, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {881799, 100, 300, 1100, 1101, 1200, 1300, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {881999, 100, 300, 1100, 1101, 1200, 1300, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {882999, 100, 300, 1100, 1101, 1200, 1300, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {900099, 100, 300, 1100, 1101, 1200, 1300, 5501, 5591, 5592, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
//            {200000, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {210000, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {230000, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {240000, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {270100, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {280000, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {500060, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {528070, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 5597, 5599, 0, 0, 0, 0, 0, 0, 0, 0},
//            {607000, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 5531, 5535, 5597, 5599, 0, 0, 0, 0, 0, 0},
//            {620070, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 5581, 5582, 5583, 5597, 0, 0, 0, 0, 0, 0, 0, 0},
//            {607090, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 5579, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {708070, 100, 300, 400, 403, 404, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 5535, 5597, 5599, 0, 0, 0, 0, 0},
//            {727080, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 5581, 5582, 5583, 5597, 5597, 5599, 0, 0, 0, 0, 0},
//            {800070, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 5509, 5533, 5597, 5599, 0, 0, 0, 0, 0, 0, 0, 0},
//            {900000, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {606100, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {620061, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {801061, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {816100, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {516108, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {510861, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {216000, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 5595, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {214100, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 5594, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {296000, 100, 300, 400, 1100, 1200, 1300, 4200, 5501, 5503, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {390061, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {390861, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 4830, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {296061, 100, 300, 1100, 1200, 1300, 4200, 5501, 5503, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {881799, 100, 300, 1100, 1200, 1300, 4200, 5503, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {881899, 100, 300, 1100, 1200, 1300, 4200, 5503, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {881999, 100, 300, 1100, 1101, 1200, 1300, 4200, 5501, 5503, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {825799, 100, 300, 409, 1100, 1200, 1300, 5501, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    public TxnPar() {
    }

    public static String createRequestFile(String processCode, String amount, String[] sICERReqValue, String t4100, String t3700, String t1300) {
        String output = "";
        int rowSize = srTnxPar.length;
        int columnSize = srTnxPar[0].length;
        //金額
        if (amount.length() == 0)
            amount = "000";
        else
            amount = amount + "00";


        //header
        output = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n";
        output += "<TransXML>\n";
        output += "\t<TRANS>\n";
        //header
        int i = 0;
        //row size
        for (i = 0; i < rowSize; i++) {
            if (srTnxPar[i][0] == Integer.parseInt(processCode))
                break;
        }
        int row = i;
        for (int j = 1; j < columnSize; j++) {
            String tempString = "";
            String tempTag = "";
            if (srTnxPar[row][j] == 0)
                break;
            else {
                switch (srTnxPar[row][j]) {
                    case 100:
                        //[row][0] = inProcessCode
                        if ((srTnxPar[row][0] / 100000) == 2)
                            tempString += "0100";
                        else if ((srTnxPar[row][0] / 100000) == 9)
                            tempString += "0500";
                        else
                            tempString += "0200";
                        break;
                    case 300:
                        tempString += srTnxPar[row][0];
                        break;
                    case 400:
                        tempString += amount;
                        break;
                    case 403:
                        tempString += amount;
                        break;
                    case 404:
                        tempString += amount;
                        break;
                    case 409:
                        tempString += amount;
                        break;
                    case 1100:
                        if (sICERReqValue[0].length() == 0) {
                            SimpleDateFormat sdFormat1 = new SimpleDateFormat("HHmmss");
                            tempString += sdFormat1.format(new Date());
                            sICERReqValue[0] = sdFormat1.format(new Date());//Retry時要保存
                        } else
                            tempString += sICERReqValue[0];
                        break;
                    case 1101://交易序號 tradeNo
                        if (sICERReqValue[1].length() == 0) {
                            SimpleDateFormat sdFormat1 = new SimpleDateFormat("HHmmss");
                            tempString += sdFormat1.format(new Date());
                        } else
                            tempString += sICERReqValue[1];
                        break;
                    case 1200:
                        //time
                        SimpleDateFormat sdFormat1 = new SimpleDateFormat("HHmmss");
                        tempString += sdFormat1.format(new Date());
                        break;
                    case 1300:
                        //date
                        SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyyMMdd");
                        tempString += sdFormat2.format(new Date());
                        break;
                    case 4108:
                        //tempString += "0";
                        tempString += sICERReqValue[2];
                        break;
                    case 4200://TM_SOUR_ID
                        tempString += "38300000";
                        break;
                    case 4830:
                        //tempString += "0";
                        tempString += sICERReqValue[3];
                        break;
                    case 5501://TM_BATCH_NO
                        //tempString += "000001";
                        tempString += sICERReqValue[4];
                        break;
                    case 5503://TM_LOCATION_ID
                        tempString += "0000000001";
                        break;
                    case 5509:
                        tempString = "E";
                        break;
                    case 5581:
                        tempString += t4100;
                        break;
                    case 5582:
                        tempString += t3700;
                        break;
                    case 5583:
                        tempString += t1300;
                        break;
                    case 5591:
                        tempString = sICERReqValue[5];
                        break;
                    case 5592:
                        tempString = sICERReqValue[6];
                        break;
                    case 5593:
                        // 帶入參數 1: 1~6筆  2:7~12筆
                        tempString = sICERReqValue[7];
                        break;
//                    case 5535:
//                        inRetVal = inGetUserInPutData("Dis Point", ucBuf, 10, 0);
//                        break;
//                    case 5581:
//                        inRetVal = inGetUserInPutData("Org Device ID", ucBuf, 9, 1);
//                        break;
//                    case 5582:
//                        inRetVal = inGetUserInPutData(" OrgRRN", ucBuf, 20, 1);
//                        break;
//                    case 5583:
//                        inRetVal = inGetUserInPutData("Org Date Time", ucBuf, 14, 1);
//                        break;
//                    case 5594:
//                        inRetVal = inGetUserInPutData("Dongle 1000 Deduct Log SN", ucBuf, 6, 1);
//                        break;
//                    case 5595:
//                        inRetVal = inGetUserInPutData("Deduct Log 1=1~6/2=7~12", ucBuf, 1, 1);
//                        break;
//                    case 5597:
//                        inRetVal = inGetUserInPutData("Is Offline? N=No Offline/Y=Offline", ucBuf, 1, 2);
//                        break;
//                    case 4830:
//                        tempString += "1";
//                        break;
//                    case 5599:
//                        inRetVal = inGetUserInPutData("Is Dis? N=No Dis/D=Dis 1/C=Dis 2", ucBuf, 1, 2);
//                        break;
                }
            }
            if (srTnxPar[row][j] < 1000)
                tempTag = "0" + srTnxPar[row][j];
            else
                tempTag = "" + srTnxPar[row][j];
            //
            if (srTnxPar[row][j] == 5592)
                output += "\t\t<T" + tempTag + ">\n" +
                        "\t\t\t<T559201>" + tempString + "00" + "</T559201>" +
                        "\n\t\t</T" + tempTag + ">" + "\n";
            else
                output += "\t\t<T" + tempTag + ">" + tempString + "</T" + tempTag + ">" + "\n";
        }
        //end
        output += "\t</TRANS>\n";
        output += "</TransXML>\n";
        //end
        return output;
    }
}
