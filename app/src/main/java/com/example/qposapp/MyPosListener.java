package com.example.qposapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.blumonpay.blumonpay_library.interfaces.OperativesListener;
import com.blumonpay.blumonpay_library.model.ObjectResponse;
import com.blumonpay.blumonpay_library.services.ServicesBPC;
import com.blumonpay.blumonpay_library.services.ServicesTokener;
import com.blumonpay.blumonpay_library.util.TransactionUtils;
import com.dspread.xpos.QPOSService;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

class MyPosListener implements QPOSService.QPOSServiceListener, OperativesListener {

    private boolean connected;
    private String bluetootAddress;
    private ServicesTokener servicesTokener;
    private QPOSService.TransactionType transactionType;
    private com.blumonpay.blumonpay_library.util.List.EntryMode entryMode;
    private com.blumonpay.blumonpay_library.util.List.Authentication authentication;
    private com.blumonpay.blumonpay_library.util.List.Currency currency;
    //private ServicesTokener servicesTokener;
    private String cancellationAmount;
    private QPOSService qpos;

    private ServicesBPC servicesBPC;
    private boolean cancelFlag = false;
    private Logger logger = Logger.getLogger(MyPosListener.class.getName());


    public void attNewLogin() {
        //ServicesTokener servicesTokener = new ServicesTokener(this, MainActivity.this);
        servicesTokener.loginUser("kevin.munoz@digifact.com.gt", "o.Am.L5LkfJI2BD");
        //Toast.makeText(getApplicationContext(), "Inserte o deslice Trajeta", Toast.LENGTH_LONG).show();
    }


    // Muestra mensaje mientras se espera respuesta del cliente
    @Override
    public void onRequestWaitingUser() {
//        ServicesTokener servicesTokener = new ServicesTokener(this, MainActivity.this);
//        servicesTokener.loginUser("kevin.munoz@digifact.com.gt", "o.Am.L5LkfJI2BD");
//        Toast.makeText(getApplicationContext(), "Inserte o deslice Trajeta", Toast.LENGTH_LONG).show();
    }

    private void getApplicationContext() {

    }

    // Extrae el numero serial del dispositivo
    @Override
    public void onQposIdResult(Hashtable<String, String> posIdTable) {
        Log.i("Entrada", "Entrada a onQposIdResult");
        String serialDevice = posIdTable.get("posId") == null ? "" : posIdTable.get("posId");
        Log.i("Entrada", "Qpos serial "+serialDevice);

        if (serialDevice != null) {

            servicesTokener.loginDevice(serialDevice, bluetootAddress);
            Log.i("Entrada", "Serial Existente");

        } else {
            Log.i("Entrada", "No se logro la coneccion con servicios tokener");
        }


    }

    @Override
    public void onQposKsnResult(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onQposIsCardExist(boolean b) {

    }

    @Override
    public void onRequestDeviceScanFinished() {

    }

    @Override
    public void onQposInfoResult(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onQposGenerateSessionKeysResult(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onQposDoSetRsaPublicKey(boolean b) {

    }

    @Override
    public void onSearchMifareCardResult(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onBatchReadMifareCardResult(String s, Hashtable<String, List<String>> hashtable) {

    }

    @Override
    public void onBatchWriteMifareCardResult(String s, Hashtable<String, List<String>> hashtable) {

    }

    // Detecta el modo de lectura de la tarjeta
    @Override
    public void onDoTradeResult(QPOSService.DoTradeResult doTradeResult, Hashtable<String, String> hashtable) {
        Log.i("Entrada", "Entrada a onDoTradeResult");
        if (doTradeResult == QPOSService.DoTradeResult.NONE) {
            //Toast.makeText(getApplicationContext(), "No se Detecto Tarjeta", Toast.LENGTH_LONG).show();
        } else if (doTradeResult == QPOSService.DoTradeResult.ICC) {
            entryMode = com.blumonpay.blumonpay_library.util.List.EntryMode.CHIP;
            //Toast.makeText(getApplicationContext(), "Tarjeta Insertada (ICC)", Toast.LENGTH_LONG).show();
            Log.i("Entrada", "Iniciando Transaccion ICC (doEmvApp)");
            qpos.doEmvApp(QPOSService.EmvOption.START);
        } else if (doTradeResult == QPOSService.DoTradeResult.NOT_ICC) {
            entryMode = com.blumonpay.blumonpay_library.util.List.EntryMode.CONTACTLESS;
            //Toast.makeText(getApplicationContext(), "Tarjeta Insertada", Toast.LENGTH_LONG).show();
        } else if (doTradeResult == QPOSService.DoTradeResult.BAD_SWIPE) {
            //Toast.makeText(getApplicationContext(), "Deslizamiento Incorrecto de Tarjeta", Toast.LENGTH_LONG).show();
        } else if (doTradeResult == QPOSService.DoTradeResult.MCR) {
            entryMode = com.blumonpay.blumonpay_library.util.List.EntryMode.MAGNETIC_STRIPE;

//                authentication = com.blumonpay.blumonpay_library.util.List.Authentication.SIGNATURE;

            if (cancelFlag = true) {
                Hashtable<String, String> decodeData;
                String tagArrStr = "575F205F2A5F349f379f369f359f349f339f279f269f1e9f1a9f109f099f039f028284959a9c";
                decodeData = qpos.getICCTag(0, 22, tagArrStr);

                servicesBPC.cancel(decodeData, cancellationAmount, entryMode, authentication);

            }

            String maskedPAN = hashtable.get("maskedPAN");
            String expiryDate = hashtable.get("expiryDate");
            String cardHolderName = hashtable.get("cardholderName");
            String ksn = hashtable.get("ksn");
            String serviceCode = hashtable.get("serviceCode");
            String track1Length = hashtable.get("track1Length");
            String track2Length = hashtable.get("track2Length");
            String track3Length = hashtable.get("track3Length");
            String encTracks = hashtable.get("encTracks");
            String encTrack1 = hashtable.get("encTrack1");
            String encTrack2 = hashtable.get("encTrack2");
            String encTrack3 = hashtable.get("encTrack3");
            String partialTrack = hashtable.get("partialTrack");
            String pinKsn = hashtable.get("pinKsn");
            String trackksn = hashtable.get("trackksn");
            String pinBlock = hashtable.get("pinBlock");
            String encPAN = hashtable.get("encPAN");
            String trackRandomNumber = hashtable.get("trackRandomNumber");
            String pinRandomNumber = hashtable.get("pinRandomNumber");


        } else if (doTradeResult == QPOSService.DoTradeResult.NO_RESPONSE) {
            //Toast.makeText(getApplicationContext(), "No se Obtuvo Respuesta", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFinishMifareCardResult(boolean b) {

    }

    @Override
    public void onVerifyMifareCardResult(boolean b) {

    }

    @Override
    public void onReadMifareCardResult(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onWriteMifareCardResult(boolean b) {

    }

    @Override
    public void onOperateMifareCardResult(Hashtable<String, String> hashtable) {

    }

    @Override
    public void getMifareCardVersion(Hashtable<String, String> hashtable) {

    }

    @Override
    public void getMifareReadData(Hashtable<String, String> hashtable) {

    }

    @Override
    public void getMifareFastReadData(Hashtable<String, String> hashtable) {

    }

    @Override
    public void writeMifareULData(String s) {

    }

    @Override
    public void verifyMifareULData(Hashtable<String, String> hashtable) {

    }

    @Override
    public void transferMifareData(String s) {

    }

    // Se establece el monto a solicitar
    @Override
    public void onRequestSetAmount() {
        Log.i("Entrada", "on request set amount");

        String amount = ".59";
        int point = amount.indexOf('.');
        int length = amount.length();
        String decimal = amount.substring(point + 1, length);
        if (decimal.length() == 1) {
            amount = amount + "0";
        }

        String amountMpos = amount.replaceAll("[.,]", "");
        String currencyCode = "484";
        String cashbackAmount = "0";
        Log.i("Entrada", "amount " + amountMpos);


        //QPOSService.TransactionType transactionType;
        //if(operationType.equals("cancelHeader")){
        //transactionType = QPOSService.TransactionType.REFUND;
        //} else {
//            transactionType = QPOSService.TransactionType.GOODS;
        //}
        transactionType = QPOSService.TransactionType.SALE;


        qpos.setAmount(amountMpos, cashbackAmount, currencyCode, transactionType);
    }

    // Choose one EMV application from the list
    @Override
    public void onRequestSelectEmvApp(ArrayList<String> arrayList) {
        qpos.selectEmvApp(0);
    }

    @Override
    public void onRequestIsServerConnected() {

    }

    @Override
    public void onRequestFinalConfirm() {

    }

    // Devuelve la informacion de la tarjeta chip detectada
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onRequestOnlineProcess(String tlv) {

        Hashtable<String, String> decodeData;
        String tagArrStr = "575F205F2A5F349f379f369f359f349f339f279f269f1e9f1a9f109f099f039f028284959a9c";
        decodeData = qpos.getICCTag(0, 22, tagArrStr);

        String decodeDataSubstring = decodeData.get("tlv");
        String amount = "123";
        int point = amount.indexOf('.');
        int length = amount.length();
        String decimal = amount.substring(point + 1, length);
        if (decimal.length() == 1) {
            amount = amount + "0";
        }

        String amountMpos = amount.replaceAll("[.,]", "");


        if (cancelFlag = true) {
            servicesBPC.cancel(decodeData, amount, entryMode, authentication);
        } else {

        }
        servicesBPC.validateTransaction(decodeData, amount, entryMode, authentication, currency);
        //servicesBPC.sale(decodeData, amount, entryMode, authentication);
        //servicesBPC.cancel(decodeData,amount, entryMode, authentication);
    }

    // Time info sent to EMV kernel when requested
    @Override
    public void onRequestTime() {
        String terminalTime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        qpos.sendTime(terminalTime);
    }

    // Devuelve el status de la transaccion
    @Override
    public void onRequestTransactionResult(QPOSService.TransactionResult transactionResult) {
        if (transactionResult == QPOSService.TransactionResult.APPROVED) {
            String tagArrStr = "575F205F2A5F349f379f369f359f349f339f279f269f1e9f1a9f109f099f039f028284959a9c";
            Hashtable<String, String> decodeData;
            decodeData = qpos.getICCTag(0, 22, tagArrStr);
            String decodeDataSubstring = decodeData.get("tlv");
            TransactionUtils transactionUtils = new TransactionUtils();
            String tag9F26 = null;
            if (decodeDataSubstring == null) {
                qpos.cancelSelectEmvApp();
            } else {
                tag9F26 = transactionUtils.getTag9F26(decodeDataSubstring);
            }
            approvedTicket(tag9F26);
            //Toast.makeText(getApplicationContext(), "Transaccion Aprobada, Retire Tarjeta", Toast.LENGTH_LONG).show();

        } else if (transactionResult == QPOSService.TransactionResult.TERMINATED) {

        } else if (transactionResult == QPOSService.TransactionResult.DECLINED) {

        }
    }

    private void approvedTicket(String tc) {

        logger.log(INFO, "Valor TC: " + tc);
        ObjectResponse ticketData = null;
        ticketData.setTc(tc);
        Intent intent = null;
        //if (ticketData.getAuthentication().equals("signature")){

        //} else {

        //}

        Gson gson = new Gson();
        String json = gson.toJson(ticketData);
        intent.putExtra("ticket data", json);

        //activity.startActivity(intent);

        String operation = "Venta";

        //if (ticketData.getTypeOperation().equals("RECIBO DE CANCELACION")){

        //}else{

        //}

//            InsertApprovedTx insertApprovedTx = new InsertApprovedTx(MainActivity.this);
//            insertApprovedTx.insertTransactions(operation,
//                    Double.parseDouble(ticketData.getMount().replaceAll("[$,]","")),
//                     ticketData.getTipPercentaje(),
//                    ticketData.getWaiter(), ticketData.getAuthorization(),
//                   ticketData.getOperationNumber(), Integer.parseInt(ticketData.getLast4()),
//                    ticketData.getTypeCard(), ticketData.getReferenceNumber().substring(0,8),
//                    dataSource.readDeviceRule().getTransactionProfile(), ticketData.getIso()
//            );

    }

    @Override
    public void onRequestTransactionLog(String s) {

    }

    @Override
    public void onRequestBatchData(String s) {

    }

    // Conexion exitosa del dispositivo con el app
    @Override
    public void onRequestQposConnected() {
        Log.i("Entrada", "Entrada a onrequestqposconnected");
        connected = true;
        // Crash when getQposId called
        qpos.getQposId();

    }

    // Indica desconexion del dispositivo con el app
    @Override
    public void onRequestQposDisconnected() {

    }

    // Conexion fallida del dispositivo con el app
    @Override
    public void onRequestNoQposDetected() {

    }

    @Override
    public void onRequestNoQposDetectedUnbond() {

    }

    // Detecta el tipo de error con respecto a dispositivo
    @Override
    public void onError(QPOSService.Error error) {

    }

    // Mensaje a mostrar en el dispositivo
    @Override
    public void onRequestDisplay(QPOSService.Display display) {

    }

    @Override
    public void onReturnReversalData(String s) {

    }

    @Override
    public void onReturnGetPinResult(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onReturnPowerOnIccResult(boolean b, String s, String s1, int i) {

    }

    @Override
    public void onReturnPowerOffIccResult(boolean b) {

    }

    @Override
    public void onReturnApduResult(boolean b, String s, int i) {

    }

    @Override
    public void onReturnSetSleepTimeResult(boolean b) {

    }

    @Override
    public void onGetCardNoResult(String s) {

    }

    @Override
    public void onRequestSignatureResult(byte[] bytes) {

    }

    @Override
    public void onRequestCalculateMac(String s) {

    }

    @Override
    public void onRequestUpdateWorkKeyResult(QPOSService.UpdateInformationResult updateInformationResult) {

    }

    @Override
    public void onReturnCustomConfigResult(boolean b, String s) {

    }

    @Override
    public void onRequestSetPin() {
        // If PIN is requested, you can send a pin or bypass with emptypin func
        //qpos.sendPin("123456");
        //qpos.emptyPin();
    }

    @Override
    public void onReturnSetMasterKeyResult(boolean b) {

    }

    @Override
    public void onRequestUpdateKey(String s) {

    }

    @Override
    public void onReturnUpdateIPEKResult(boolean b) {

    }

    @Override
    public void onReturnRSAResult(String s) {

    }

    @Override
    public void onReturnUpdateEMVResult(boolean b) {

    }

    @Override
    public void onReturnGetQuickEmvResult(boolean b) {

    }

    @Override
    public void onReturnGetEMVListResult(String s) {

    }

    @Override
    public void onReturnUpdateEMVRIDResult(boolean b) {

    }

    @Override
    public void onDeviceFound(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onReturnBatchSendAPDUResult(LinkedHashMap<Integer, String> linkedHashMap) {

    }

    @Override
    public void onBluetoothBonding() {

    }

    @Override
    public void onBluetoothBonded() {

    }

    @Override
    public void onWaitingforData(String s) {

    }

    @Override
    public void onBluetoothBondFailed() {

    }

    @Override
    public void onBluetoothBondTimeout() {

    }

    @Override
    public void onReturniccCashBack(Hashtable<String, String> hashtable) {

    }

    @Override
    public void onLcdShowCustomDisplay(boolean b) {

    }

    @Override
    public void onUpdatePosFirmwareResult(QPOSService.UpdateInformationResult updateInformationResult) {

    }

    @Override
    public void onBluetoothBoardStateResult(boolean b) {

    }

    @Override
    public void onReturnDownloadRsaPublicKey(HashMap<String, String> hashMap) {

    }

    @Override
    public void onGetPosComm(int i, String s, String s1) {

    }

    @Override
    public void onUpdateMasterKeyResult(boolean b, Hashtable<String, String> hashtable) {

    }

    @Override
    public void onPinKey_TDES_Result(String s) {

    }

    @Override
    public void onEmvICCExceptionData(String s) {

    }

    @Override
    public void onSetParamsResult(boolean b, Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onGetInputAmountResult(boolean b, String s) {

    }

    @Override
    public void onReturnNFCApduResult(boolean b, String s, int i) {

    }

    @Override
    public void onReturnPowerOnNFCResult(boolean b, String s, String s1, int i) {

    }

    @Override
    public void onReturnPowerOffNFCResult(boolean b) {

    }

    @Override
    public void onCbcMacResult(String s) {

    }

    @Override
    public void onReadBusinessCardResult(boolean b, String s) {

    }

    @Override
    public void onWriteBusinessCardResult(boolean b) {

    }

    @Override
    public void onConfirmAmountResult(boolean b) {

    }

    @Override
    public void onSetManagementKey(boolean b) {

    }

    @Override
    public void onSetSleepModeTime(boolean b) {

    }

    @Override
    public void onGetSleepModeTime(String s) {

    }

    @Override
    public void onGetShutDownTime(String s) {

    }

    @Override
    public void onEncryptData(String s) {

    }

    @Override
    public void onAddKey(boolean b) {

    }

    @Override
    public void onSetBuzzerResult(boolean b) {

    }

    @Override
    public void onSetBuzzerTimeResult(boolean b) {

    }

    @Override
    public void onSetBuzzerStatusResult(boolean b) {

    }

    @Override
    public void onGetBuzzerStatusResult(String s) {

    }

    @Override
    public void onQposDoTradeLog(boolean b) {

    }

    @Override
    public void onQposDoGetTradeLogNum(String s) {

    }

    @Override
    public void onQposDoGetTradeLog(String s, String s1) {

    }

    @Override
    public void onRequestDevice() {

    }

    @Override
    public void onGetKeyCheckValue(List<String> list) {

    }

    @Override
    public void onGetDevicePubKey(String s) {

    }

    @Override
    public void onSetPosBlePinCode(boolean b) {

    }

    @Override
    public void onTradeCancelled() {

    }

    @Override
    public void onReturnSetAESResult(boolean b, String s) {

    }

    @Override
    public void onReturnAESTransmissonKeyResult(boolean b, String s) {

    }

    @Override
    public void onReturnSignature(boolean b, String s) {

    }

    @Override
    public void onReturnConverEncryptedBlockFormat(String s) {

    }

    @Override
    public void onQposIsCardExistInOnlineProcess(boolean b) {

    }

    // Actualizar Transaccion
    private void seeMap() {

    }

    public void validateCancellation(String operationNumber) {
        //servicesBPC.validateCancellation(operationNumber);
        // Implementacion correcta?
        servicesBPC.validateCancel(Integer.valueOf(operationNumber));
    }


    // Metodos transaccionales


    @Override
    public void startTransaction() {
        logger.log(Level.INFO, "INICIANDO SERVICIO");
    }

    @Override
    public void processingTransaction() {
        logger.log(Level.INFO, "PROCESANDO SERVICIO");
    }

    @Override
    public void endTransaction(boolean transactionStatus, String description, ObjectResponse objectResponse) {

        boolean statusTransaction = transactionStatus;
        if (transactionStatus){
            if (description.equals("RESPONSE LOGIN DEVICE")) {
                logger.log(INFO, "Response login Device object"+objectResponse.getMessage1());
                logger.log(INFO, "Response login Device descripcion"+description);

            }
        }
        //Cancelacion
        if (cancelFlag = true) {
            cancellationAmount = objectResponse.getMount();
            qpos.doCheckCard(30);
            transactionType = QPOSService.TransactionType.REFUND;
            qpos.setAmount(cancellationAmount, "0", "484", transactionType);
        }


    }

}