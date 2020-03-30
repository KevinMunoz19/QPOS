package com.example.qposapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blumonpay.blumonpay_library.interfaces.OperativesListener;
import com.blumonpay.blumonpay_library.model.ObjectResponse;
import com.blumonpay.blumonpay_library.services.ServicesTokener;
import com.blumonpay.blumonpay_library.util.InsertApprovedTx;
import com.blumonpay.blumonpay_library.util.TransactionUtils;
import com.dspread.xpos.QPOSService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;


public class MainActivity extends AppCompatActivity{

    private Logger logger = Logger.getLogger(QposFunctions.class.getName());
    private List<BluetoothDevice> lstDevScanned;
    private boolean isNormalBlu;
    BluetoothAdapter bluetoothAdapter;
    private boolean isDeviceEnabled=false;
    private EditText deviceBtName;
    private EditText deviceBtAddress;
    private Button searchDevice;
    private Button connectDevice;
    private QPOSService.QPOSServiceListener listener;
    private QPOSService qpos;
    private String bluetootAddress;
    private String serialDevice;
    private boolean connected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceBtName = (EditText) findViewById(R.id.deviceName);
        deviceBtAddress = (EditText) findViewById(R.id.deviceAddress);
        searchDevice = (Button) findViewById(R.id.searchDeviceBtn);
        searchDevice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Entrada","Boton Presionado Buscar");
                scanBluetoothdevice();
            }
        });
        connectDevice = (Button) findViewById(R.id.connectDeviceBtn);
        connectDevice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Entrada","Boton Presionado Connectar");
                String btadd = String.valueOf(deviceBtAddress.getText());
                connectionDeviceBT(btadd);
            }
        });

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (null==bluetoothAdapter){
            Toast.makeText(this, "Bluetooth Missing", Toast.LENGTH_SHORT).show();
        }else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(intent,REQUESTCODE);
            } else {
                //noOfPairedDevices();
            }
        }
    }

    private void openConnection(QPOSService.CommunicationMode mode) {

        //QposFunctions lis = new QposFunctions(this, listener);
        qpos = QPOSService.getInstance(mode);
        if (qpos == null){
            return;
        }
        qpos.setContext(this);
        Handler handler = new Handler(Looper.myLooper());
        qpos.initListener(handler,listener);
    }


    private void connectionDeviceBT(String addressDevice) {


        openConnection(QPOSService.CommunicationMode.BLUETOOTH);
        bluetootAddress = addressDevice;

        qpos.connectBluetoothDevice(true,25,bluetootAddress);

        //qposf.onRequestQposConnected();

    }



    // Create a BroadcastReceiver for ACTION_FOUND.
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i("Entrada","Entrada a onReceive");
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i("Entrada","Nombre"+deviceName);
                Log.i("Entrada","Direccion"+deviceHardwareAddress);

                deviceBtName.setText(deviceName);
                deviceBtAddress.setText(deviceHardwareAddress);

                Toast.makeText(getApplicationContext(),"Se Encontro Dispositivo",Toast.LENGTH_LONG).show();

                bluetoothAdapter.cancelDiscovery();
            }
        }
    };

    private  void scanBluetoothdevice(){
        Log.i("Entrada","scan bt devices");
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (!isDeviceEnabled){
            this.registerReceiver(broadcastReceiver,filter);
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
        bluetoothAdapter.cancelDiscovery();
    }


    class MyPosListener implements QPOSService.QPOSServiceListener, OperativesListener {
        // Muestra el monto en el dispositivo
        @Override
        public void onRequestWaitingUser() {

        }

        // Extrae el numero serial del dispositivo
        @Override
        public void onQposIdResult(Hashtable<String, String> posIdTable) {
            serialDevice = posIdTable.get("posId") == null ? "" : posIdTable.get("posId");

            if (serialDevice != null){
                ServicesTokener servicesTokener = new ServicesTokener(this, MainActivity.this);
                servicesTokener.loginDevice(serialDevice, bluetootAddress);
            } else {
                //qposListener.StartAlert(errorHeader, "No se puede obtener el serial del dispositivo");
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

        }

        @Override
        public void onRequestSelectEmvApp(ArrayList<String> arrayList) {

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
            decodeData = qpos.getICCTag(0,22,tagArrStr);

            String decodeDataSubstring = decodeData.get("tlv");

            //servicesBPC.sale(decodeData, amount, entryMode, authentication);
            //servicesBPC.cancel(decodeData,amount, entryMode, authentication);

        }

        @Override
        public void onRequestTime() {

        }

        // Devuelve el status de la transaccion
        @Override
        public void onRequestTransactionResult(QPOSService.TransactionResult transactionResult) {

            if (transactionResult == QPOSService.TransactionResult.APPROVED){

                String tagArrStr = "575F205F2A5F349f379f369f359f349f339f279f269f1e9f1a9f109f099f039f028284959a9c";
                Hashtable<String, String> decodeData;
                decodeData = qpos.getICCTag(0,22,tagArrStr);
                String decodeDataSubstring = decodeData.get("tlv");

                TransactionUtils transactionUtils = new TransactionUtils();
                String tag9F26 = null;
                if (decodeDataSubstring == null) {
                    qpos.cancelSelectEmvApp();
                } else {
                    tag9F26 = transactionUtils.getTag9F26(decodeDataSubstring);
                }

                approvedTicket(tag9F26);


            }else if (transactionResult == QPOSService.TransactionResult.TERMINATED) {

            } else if (transactionResult == QPOSService.TransactionResult.DECLINED){

            }
        }

        private void approvedTicket(String tc) {
            logger.log(INFO, "Valor TC: "+tc);

            //ticketData.setTc(tc);

            Intent intent = null;
            //if (ticketData.getAuthentication().equals("signature")){

            //} else {

            //}

            Gson gson = new Gson();
            //String json = gson.toJson(ticketData);
            //intent.putExtra("ticket data", json);

            //activity.startActivity(intent);

            String operation = "Venta";

            //if (ticketData.getTypeOperation().equals("RECIBO DE CANCELACION")){

            //}else{

            //}

            //InsertApprovedTx insertApprovedTx = new InsertApprovedTx(activity);
            //insertApprovedTx.insertTransactions(operation,
            //        Double.parseDouble(ticketData.getMount().replaceAll("[$,]","")),
            //        ticketData.getTipAmount(), ticketData.getTipPercentaje(),
            //        ticketData.getWaiter(), ticketData.getAuthorization(),
            //       ticketData.getOperationNumber(), Integer.parseInt(ticketData.getLast4()),
            //        ticketData.getTypeCard(), ticketData.getReferenceNumber().substring(0,8),
            //        dataSource.readDeviceRule().getTransactionProfile(), ticketData.getIso()
            //);

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
            Log.i("Entrada","Entrada a onrequestqposconnected");
            connected = true;
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
        private void seeMap(){


        }

        public void validateCancellation(String operationNumber){
            //servicesBPC.validateCancellation(operationNumber);
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

        }
    }







}
