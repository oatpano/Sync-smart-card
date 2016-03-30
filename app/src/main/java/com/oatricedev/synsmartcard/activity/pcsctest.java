package com.oatricedev.synsmartcard.activity;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.oatricedev.synsmartcard.R;

import java.util.ArrayList;
import java.util.List;

class winscard
{
	String LOG_TAG = "winscard";
	static{
		System.loadLibrary("snappydb-native");
		}
	public native long SCardEstablishContext(long dwScope, long phContext[]);
	public native long SCardReleaseContext(long hContext);
	public native long SCardIsValidContext(long hContext);
	public native long SCardSetTimeout(long hContext, long dwTimeout);
	public native long SCardConnect(long hContext, final char szReader[], long dwShareMode, long dwPreferredProtocols, long phCard[], long pdwActiveProtocol[]);
	public native long SCardReconnect(long hCard, long dwShareMode, long dwPreferredProtocols, long dwInitialization, long pdwActiveProtocol[]);
	public native long SCardDisconnect(long hCard, long dwDisposition);
	public native long SCardBeginTransaction(long hCard);
	public native long SCardEndTransaction(long hCard, long dwDisposition);
	public native long SCardCancelTransaction(long hCard);
	public native long SCardStatus(long hCard, char mszReaderName[], long pcchReaderLen[], long pdwState[], long pdwProtocol[], char pbAtr[], long pcbAtrLen[]);
	public native long SCardGetStatusChange(long hContext, long dwTimeout, final char szReader[], long dwCurrentState[], long dwEventState[], long cbAtr[], char rgbAtr[], long cReaders);
	public native long SCardControl(long hCard, long dwControlCode, final char pbSendBuffer[], long cbSendLength, char pbRecvBuffer[], long cbRecvLength, long lpBytesReturned[]);
	public native long SCardTransmit(long hCard, long dwSendPciProtocol, long cbSendPciLength, final char pbSendBuffer[], long cbSendLength, long dwRecvPciProtocol[], long cbRecvPciLength[], char pbRecvBuffer[], long pcbRecvLength[]);
	public native long SCardListReaderGroups(long hContext, char mszGroups[], long pcchGroups[]);
	public native long SCardListReaders(long hContext, final char mszGroups[], char mszReaders[], long pcchReaders[]);
	public native long SCardCancel(long hContext);
	public native long SCardGetAttrib(long hCard, long dwAttrId, char pbAttr[], long pcbAttrLen[]);
	public native long SCardSetAttrib(long hCard, long dwAttrId, final char pbAttr[], long cbAttrLen);
    
	public native long AlcorSwitchCardMode(long lngCard, char bSlotNum, char bCardMode);
	public native long SLE4442CmdReadMainMemory(long lngCard, char bSlotNum, char bAddress, char bReadLen, char pcReadData[], long plReturnLen[]);
	
	public 		long dwScope = 0;
	public      long phContext[] = new long[16];
	public      long hContext;
	public      long dwTimeout;
	public      long lngResult = 0;
	
	public 		char szReader[] = new char[128];
	public 		long dwShareMode;
	public 		long dwPreferredProtocols;
	public 		long phCard[] = new long[16];
	public      long hCard;
	public 		long pdwActiveProtocol[] = new long[1];
	public 		long pcchGroups[] = new long[16];
	public      char mszGroups[] = new char[128];
	
	public      char mszReaders[] = new char[128];
	public      char mszReader0[] = new char[64];
	public      char mszReader1[] = new char[64];
	public      long pcchReaders[] = new long[16];
	public      int  i;
	
	public      char pbAttr[] = new char[128];
	public      long dwAttrId = 0;
	public      long pcbAttrLen[] = new long[2];
	
	public      long pcbRecvLength[] = new long[2];
	
	public      int nReaderNum = 0;
	public      char pbRecvBuffer[] = new char[128];
	
	public String strTemp;
	private String strTemp1;
	public winscard()
	{
	}

	public long TestSCardEstablishContext()
	{
		Log.d(LOG_TAG, "TestSCardEstablishContext");
		phContext[0] = 2;
		return SCardEstablishContext(dwScope, phContext);
	}
	
	public long TestSCardReleaseContext()
	{
		Log.d(LOG_TAG, "TestSCardReleaseContext");
		hContext = phContext[0];
		return SCardReleaseContext(hContext);
	}
	
	public long TestSCardIsValidContext()
	{
		Log.d(LOG_TAG, "TestSCardIsValidContext");
		hContext = phContext[0];
		return SCardIsValidContext(hContext);
	}
	
	public long TestSCardSetTimeout()
	{
		Log.d(LOG_TAG, "TestSCardSetTimeout");
		hContext = phContext[0];
		return SCardSetTimeout(hContext, 2);
	}
	
	public long TestSCardConnect(int nReaderIndex, int nPowerOn)
	{
		Log.d(LOG_TAG, "TestSCardConnect");
		hContext = phContext[0];
		//We just test reader 0.
		switch (nReaderIndex)
		{
		case 0:
			for(i=0; i<64; i++)
			{
				szReader[i] = mszReader0[i];
				if(szReader[i] =='\0')
					break;
			}
			break;
		case 1:
			for(i=0; i<64; i++)
			{
				szReader[i] = mszReader1[i];
				if(szReader[i] =='\0')
					break;
			}
			break;
		default:
			break;
		}
		
		for(; i<128; i++)
			szReader[i] = 0;
		
//		#define SCARD_PROTOCOL_UNDEFINED	0x0000	/**< protocol not set */
//		#define SCARD_PROTOCOL_T0		0x0001	/**< T=0 active protocol. */
//		#define SCARD_PROTOCOL_T1		0x0002	/**< T=1 active protocol. */
//		#define SCARD_PROTOCOL_RAW		0x0004	/**< Raw active protocol. */
//		#define SCARD_PROTOCOL_T15		0x0008	/**< T=15 protocol. */	
		
		
//		#define SCARD_SHARE_EXCLUSIVE		0x0001	/**< Exclusive mode only */
//		#define SCARD_SHARE_SHARED		0x0002	/**< Shared mode only */
//		#define SCARD_SHARE_DIRECT		0x0003	/**< Raw mode only */
		if(nPowerOn != 0)
		{
			dwPreferredProtocols = 0x0001|0x0002;//T0&T1
			dwShareMode = 0x02; //SCARD_SHARE_SHARED
		}
		else
		{
			dwPreferredProtocols = 0x0;//SCARD_PROTOCOL_UNDEFINED
			dwShareMode = 0x03; //SCARD_SHARE_DIRECT
		}

		lngResult = SCardConnect(hContext, szReader, dwShareMode, dwPreferredProtocols, phCard, pdwActiveProtocol);
		if(pdwActiveProtocol[0] == 0x01)
    		strTemp = "ActiveProtocol: T0";
    	if(pdwActiveProtocol[0] == 0x02)
    		strTemp = "ActiveProtocol: T1";
    	Log.d(LOG_TAG, strTemp);
    	return lngResult;
	}
	
	public long TestSCardReconnect()
	{
		Log.d(LOG_TAG, "TestSCardReconnect");
		long dwInitialization = 0x02; //SCARD_UNPOWER_CARD
		hCard = phCard[0];
		dwPreferredProtocols = 0x01|0x02;
		
		lngResult = SCardReconnect(hCard, dwShareMode, dwPreferredProtocols,
				dwInitialization, pdwActiveProtocol);

		if(pdwActiveProtocol[0] == 0x01)
    		strTemp = "ActiveProtocol: T0";
    	if(pdwActiveProtocol[0] == 0x02)
    		strTemp = "ActiveProtocol: T1";
    	Log.d(LOG_TAG, strTemp);
    	
		return lngResult;
	}
	
	public long TestSCardDisconnect()
	{
		Log.d(LOG_TAG, "TestSCardDisconnect");
//		#define SCARD_LEAVE_CARD		0x0000	/**< Do nothing on close */
//		#define SCARD_RESET_CARD		0x0001	/**< Reset on close */
//		#define SCARD_UNPOWER_CARD		0x0002	/**< Power down on close */
//		#define SCARD_EJECT_CARD		0x0003	/**< Eject on close */
		long lngDisposition = 0x0001;
		return SCardDisconnect(hCard, lngDisposition);
	}

	public long TestSCardBeginTransaction()
	{
		Log.d(LOG_TAG, "TestSCardBeginTransaction");
		hCard = phCard[0];
		return SCardBeginTransaction(hCard);
	}
	
	public long TestSCardEndTransaction()
	{
		Log.d(LOG_TAG, "TestSCardEndTransaction");
		long dwDisposition = 0x0000;//SCARD_LEAVE_CARD 
		hCard = phCard[0];
		return SCardEndTransaction(hCard, dwDisposition);
	}
	
	public long TestSCardCancelTransaction()
	{
		Log.d(LOG_TAG, "TestSCardCancelTransaction");
		hCard = phCard[0];
		return SCardCancelTransaction(hCard);
	}
	
	public long TestSCardStatus()
	{
		Log.d(LOG_TAG, "TestSCardStatus");
		return 0;
	}
	
	public long TestSCardGetStatusChange()
	{
		Log.d(LOG_TAG, "TestSCardGetStatusChange");
		return 0;
	}
	
	public long TestSCardControl()
	{
		Log.d(LOG_TAG, "TestSCardControl");
		long dwControlCode = 0x42000000 + 0x01;
		char pbSendBuffer[] = new char[128];
		long cbSendLength = 0;
		char pbRecvBuffer[] = new char[128];
		long cbRecvLength = 128;
		long lpBytesReturned[] = new long[2];
		lngResult = SCardControl(hCard, dwControlCode, pbSendBuffer, cbSendLength,
				pbRecvBuffer, cbRecvLength, lpBytesReturned);
		return lngResult;
	}
	
	public long TestSCardTransmit()
	{
		hCard = phCard[0];
		char pbSendBuffer[] = new char[128];
	
		long cbSendPciLength;
		long dwSendPciProtocol = 0x01;
		
		long dwRecvPciProtocol[] = new long[2];
		long cbRecvPciLength[] = new long[2];
		
		cbRecvPciLength[0] = cbSendPciLength = 0x08;
		
		pcbRecvLength[0] = 128;
		
		/* APDU select file */
		//00 B1 01 02 01 01
		Log.d(LOG_TAG, "Case 1:");
		pbSendBuffer[0] = 0xA0;
		pbSendBuffer[1] = 0xA4;
		pbSendBuffer[2] = 0x00;
		pbSendBuffer[3] = 0x00;
		pbSendBuffer[4] = 0x02;
		pbSendBuffer[5] = 0x3F;
		pbSendBuffer[6] = 0x00;
//		pbSendBuffer[4] = (char)cbSendPciLength;
		
		dwSendPciProtocol = pdwActiveProtocol[0];
		Log.d(LOG_TAG, "TestSCardTransmit");
		lngResult = SCardBeginTransaction(hCard);
		lngResult = SCardTransmit(hCard, dwSendPciProtocol, cbSendPciLength, pbSendBuffer, cbSendPciLength, dwRecvPciProtocol, cbRecvPciLength, pbRecvBuffer, pcbRecvLength);
		
		int nTest = 0;
		strTemp = String.format("card response len %X: ", pcbRecvLength[0]);
		for (i=0; i<pcbRecvLength[0]; i++)
    	{
    		strTemp1 = strTemp;
    		nTest = pbRecvBuffer[i] & 0xFF;
    		strTemp = strTemp1 + String.format("0x%02X ", nTest);
    	}
		Log.d(LOG_TAG, strTemp);
		
		lngResult = SCardEndTransaction(hCard, 0);
//		#define SCARD_LEAVE_CARD      0 // Don't do anything special on close
//		#define SCARD_RESET_CARD      1 // Reset the card on close
//		#define SCARD_UNPOWER_CARD    2 // Power down the card on close
//		#define SCARD_EJECT_CARD      3 // Eject the card on close		
		return lngResult;
	}
	
	public long TestSCardListReaderGroups()
	{
		Log.d(LOG_TAG, "TestSCardListReaderGroups");
		pcchGroups[0] = 128;
		for(i=0; i<128; i++)
			mszGroups[i] = 0;
		
		lngResult = SCardListReaderGroups(hContext, mszGroups, pcchGroups);
		
		strTemp = String.format("Group %d: ", pcchGroups[0]);
    	for(i=0; i<pcchGroups[0]; i++)
    	{
    		strTemp1 = strTemp;
    		strTemp = strTemp1 + String.format("%c", mszGroups[i]);
    	}
    	Log.d("pcsctest", strTemp);
    	return lngResult;
	}
	
	public long TestSCardListReaders()
	{
		Log.d(LOG_TAG, "TestSCardListReaders");
		nReaderNum = 0;
		pcchReaders[0] = 128;
		lngResult = SCardListReaders(hContext, mszGroups, mszReaders, pcchReaders);
		strTemp = String.format("ListReaders Len %x: \n Reader %d:", pcchReaders[0], nReaderNum);
		
		int nTemp = 0;
    	for(i=0; i<pcchReaders[0]; i++)
    	{
    		strTemp1 = strTemp;
    		if(mszReaders[i] == '\0')
    		{
    			nReaderNum++;
    			if(nReaderNum == 1)
    			{
    				nTemp = i+1;
    				mszReader0[i] = '\0';
    			}
    			if(nReaderNum == 2)
    				mszReader1[i-nTemp] = '\0';
    			
				if(nReaderNum > 2)
				{
					Log.d("pcsctest", strTemp);
					return lngResult;
				}
    			strTemp = strTemp1 + String.format("\n Reader %d:", nReaderNum);
    		}
    		else
    		{
    			strTemp = strTemp1 + String.format("%c", mszReaders[i]);
    			if(nReaderNum == 0)
    				mszReader0[i] = mszReaders[i];
    			else
    				mszReader1[i-nTemp] = mszReaders[i];
    		}
    	}

    	Log.d("pcsctest", strTemp);
    	
    	return lngResult;
	}
	
	public long TestSCardCancel()
	{
		Log.d(LOG_TAG, "TestSCardCancel");
		hContext = phContext[0];
		return SCardCancel(hContext);
	}
	
	public long TestSCardGetAttrib()
	{
		Log.d(LOG_TAG, "TestSCardGetAttrib");
		hCard = phCard[0];
		int nTest = 0;
		dwAttrId = 0x90303;//SCARD_ATTR_ATR_STRING
		pcbAttrLen[0] = 128;
		lngResult = SCardGetAttrib(hCard, dwAttrId, pbAttr, pcbAttrLen);
		strTemp = String.format("ATR %d: ", pcbAttrLen[0]);
    	for(i=0; i<pcbAttrLen[0]; i++)
    	{
    		strTemp1 = strTemp;
    		nTest = pbAttr[i] & 0xFF;
    		strTemp = strTemp1 + String.format("0x%02X ", nTest);
    	}
    	Log.d("pcsctest", strTemp);
    	return lngResult;
	}
	
	public long TestSCardSetAttrib()
	{
		Log.d(LOG_TAG, "TestSCardSetAttrib");
		return 0;
	}
	
	public long TestAlcorSwitchCardMode(char bSlotNum, char bCardMode)
	{
		//#define ASYNCHRONOUS_CARD_MODE			0x01
		//#define I2C_CARD_MODE					       0x02
		//#define SYNCHRONOUS_CARD_SLE4428_MODE	0x03
		//#define SYNCHRONOUS_CARD_SLE4442_MODE	0x04
		//#define AT88SC_CARD_MODE				0x05
		//#define INPHONE_CARD_MODE				0x06
		//#define AT45D041_CARD_MODE				0x07
		//#define SLE6636E_CARD_MODE              0x08
		//#define AT88SC102_CARD_MODE             0x09
		//#define SUPPORTED_MAX_EX_CARD_NUM       0x09
		
		//#define IOCTL_CCID_ESCAPE                           (FILE_DEVICE_SMARTCARD+0x36B0)
	
		
		hCard = phCard[0];
		
		lngResult = AlcorSwitchCardMode(
			hCard,
			bSlotNum,
			bCardMode
			);

		return lngResult;
	}
	
	public long TestSLE4442CmdReadMainMemory()
	{ 
		hCard = phCard[0];
		char bSlotNum = 0;
		char bAddress = 0;
		char bReadLen = 16;
		char pcReadData[] = new char [16];
		long plReturnLen[] = new long[2];
		int i = 0;
		lngResult = SLE4442CmdReadMainMemory(hCard, bSlotNum, bAddress, bReadLen, pcReadData, plReturnLen);
		
		strTemp = String.format("SLE4442CmdReadMainMemory read %d: ", plReturnLen[0]);
    	for(i=0; i<16; i++)
    	{
    		strTemp1 = strTemp;
    		strTemp = strTemp1 + String.format("0x%02X ", (short)pcReadData[i]);
    	}
		
		return lngResult;
	}
}

public class pcsctest extends Activity implements OnClickListener {
	  
	private TextView mTextView0;
	private TextView mTextView1;
	private TextView mTextView2;
	private Button ConnectButton; 
	private Button APDUButton; 
	private Button SLE4442Button; 
	private winscard mywinscard;
	private long lResult;
	private String strTemp;
	private String strTemp1;
	private String strTemp2;
	private Spinner mySpinner;
	private ArrayAdapter<String> adapter;
	private static final String[] countriesStr = 
	{"Reader Name"};
	private List<String> allCountries;
	String NewReader;
//	private int i;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupViews();
        mywinscard = new winscard();
    }
    
    public void setupViews(){   
        
    	mTextView0 = (TextView)findViewById(R.id.text);
    	mTextView1 = (TextView)findViewById(R.id.text1);
    	mTextView2 = (TextView)findViewById(R.id.text2);
        mySpinner = (Spinner)findViewById(R.id.spinner);
        
        allCountries = new ArrayList<String>();
        for(int i=0; i<countriesStr.length; i++)
        {
        	allCountries.add(countriesStr[i]);
        }
        adapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, allCountries);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	mySpinner.setAdapter(adapter);
    	
    	ConnectButton = (Button)findViewById(R.id.Test);      
    	ConnectButton.setOnClickListener(this); 
    	
    	APDUButton = (Button)findViewById(R.id.apdu);      
    	APDUButton.setOnClickListener(this); 
    	
    	SLE4442Button = (Button)findViewById(R.id.sle4442);      
    	SLE4442Button.setOnClickListener(this); 
    }
    
    public void onClick(View v) {   
        // TODO Auto-generated method stub   
        if(v == ConnectButton){ 
        	lResult = mywinscard.TestSCardEstablishContext();
        	if(lResult != 0)
        	{
        		strTemp = String.format("SCardEstablishContext fail. error code: %x\n", lResult);
        		mTextView0.setText(strTemp);
        		return;
        	}
        	strTemp = String.format("SCardEstablishContext Success. phContext = %x\n", mywinscard.phContext[0]);
        	mTextView0.setText(strTemp);
        	Log.d("pcsctest", strTemp);
        	strTemp1 = strTemp;
        	
        	
        	lResult = mywinscard.TestSCardIsValidContext();
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SCardIsValidContext fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView0.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SCardIsValidContext Success.\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	mTextView0.setText(strTemp);
        	Log.d("pcsctest", strTemp);
        	
        	lResult = mywinscard.TestSCardListReaderGroups();
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SCardListReaderGroups fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView0.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SCardListReaderGroups Success.\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	mTextView0.setText(strTemp);
 	
        	lResult = mywinscard.TestSCardListReaders();
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SCardListReaders fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView0.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SCardListReaders Success.\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	mTextView0.setText(strTemp);
        	
//        	adapter.remove(mySpinner.getSelectedItem().toString());
        	int n = adapter.getCount();
        	for(int i=0; i<n; i++)
        		adapter.remove(adapter.getItem(0).toString());
        		
        	NewReader = new String(mywinscard.mszReader0);
        	adapter.add(NewReader);
        	NewReader = new String(mywinscard.mszReader1);
        	adapter.add(NewReader);		
        }  
        
        if(v == APDUButton){ 
        	int nReaderIndex = mySpinner.getSelectedItemPosition();
        	lResult = mywinscard.TestSCardConnect(nReaderIndex, 0);
        	if(lResult != 0)
        	{
        		strTemp = String.format("SCardConnect fail. error code: %x\n", lResult);
            	strTemp1 = strTemp;
        		mTextView1.setText(strTemp);
        		return;
        	}
        	strTemp = "SCardConnect no power Success.\n";
        	strTemp1 = strTemp;
        	mTextView1.setText(strTemp);
        	char bMode = 1;
        	char bSlotNum = (char)nReaderIndex;
        	lResult = mywinscard.TestAlcorSwitchCardMode(bSlotNum, bMode);
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("Alcor_SwitchCardMode fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView1.setText(strTemp);
        		return;
        	}
        	strTemp2 = "Alcor_SwitchCardMode Success.\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	mTextView1.setText(strTemp);

        	lResult = mywinscard.TestSCardConnect(nReaderIndex, 1);
        	if(lResult != 0)
        	{
        		strTemp = String.format("SCardConnect fail. error code: %x\n", lResult);
        		mTextView1.setText(strTemp);
        		return;
        	}
        	strTemp = "SCardConnect Success.\n";
        	strTemp1 = strTemp;
        	if(mywinscard.pdwActiveProtocol[0] == 0x01)
        		strTemp2 = "ActiveProtocol: T0\n";
        	if(mywinscard.pdwActiveProtocol[0] == 0x02)
        		strTemp2 = "ActiveProtocol: T1\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	mTextView1.setText(strTemp);
        	lResult = mywinscard.TestSCardGetAttrib();
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SCardGetAttrib fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView1.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SCardGetAttrib Success\nATR:";
        	strTemp = strTemp1 + strTemp2;
        	
        	for(int i=0; i<mywinscard.pcbAttrLen[0]; i++)
        	{
        		strTemp1 = strTemp;
        		int nTest = mywinscard.pbAttr[i] & 0xFF;
        		strTemp = strTemp1 + String.format("0x%02X ", nTest);
        	}
        	strTemp1 = strTemp + "\n";
        	mTextView1.setText(strTemp);
        	lResult = mywinscard.TestSCardTransmit();
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SCardTransmit fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView1.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SCardTransmit Success.\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp  + "Send Data: 0xA0 0xA4 0x00 0x00 0x02 0x3F 0x00\n";
        	int nTemp0 = mywinscard.pbRecvBuffer[0];
        	int nTemp1 = mywinscard.pbRecvBuffer[1];
        	strTemp2 = String.format("card response len 0x%X: 0x%X 0x%X", mywinscard.pcbRecvLength[0], nTemp0, nTemp1);
        	strTemp = strTemp1 + strTemp2;
        	mTextView1.setText(strTemp);
        }
        
        if(v == SLE4442Button){
        	int nReaderIndex = mySpinner.getSelectedItemPosition();
        	lResult = mywinscard.TestSCardConnect(nReaderIndex, 0);
        	if(lResult != 0)
        	{
        		strTemp = String.format("SCardConnect fail. error code: %x\n", lResult);
            	strTemp1 = strTemp;
        		mTextView2.setText(strTemp);
        		return;
        	}
        	strTemp = "SCardConnect no power Success.\n";
        	strTemp1 = strTemp;
        	mTextView2.setText(strTemp);
        	char bMode = 4;
        	char bSlotNum = (char)nReaderIndex;
        	lResult = mywinscard.TestAlcorSwitchCardMode(bSlotNum, bMode);
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("Alcor_SwitchCardMode fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView2.setText(strTemp);
        		return;
        	}
        	strTemp2 = "Alcor_SwitchCardMode Success.\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	mTextView2.setText(strTemp);
        	
        	lResult = mywinscard.TestSCardConnect(nReaderIndex, 1);
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SCardConnect fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
        		mTextView2.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SCardConnect power on Success.\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	if(mywinscard.pdwActiveProtocol[0] == 0x01)
        		strTemp2 = "ActiveProtocol: T0\n";
        	if(mywinscard.pdwActiveProtocol[0] == 0x02)
        		strTemp2 = "ActiveProtocol: T1\n";
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp;
        	mTextView2.setText(strTemp);
        	lResult = mywinscard.TestSCardGetAttrib();
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SCardGetAttrib fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView2.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SCardGetAttrib Success\nATR:";
        	strTemp = strTemp1 + strTemp2;
        	
        	for(int i=0; i<mywinscard.pcbAttrLen[0]; i++)
        	{
        		strTemp1 = strTemp;
        		int nTest = mywinscard.pbAttr[i] & 0xFF;
        		strTemp = strTemp1 + String.format("0x%02X ", nTest);
        	}
        	strTemp1 = strTemp + "\n";
        	mTextView2.setText(strTemp);
        	
        	lResult = mywinscard.TestSLE4442CmdReadMainMemory();
        	if(lResult != 0)
        	{
        		strTemp2 = String.format("SLE4442CmdReadMainMemory fail. error code: %x\n", lResult);
        		strTemp = strTemp1 + strTemp2;
            	strTemp1 = strTemp;
        		mTextView2.setText(strTemp);
        		return;
        	}
        	strTemp2 = "SLE4442CmdReadMainMemory Success\n" + mywinscard.strTemp;
        	strTemp = strTemp1 + strTemp2;
        	strTemp1 = strTemp + "\n";
        	mTextView2.setText(strTemp);
        }
    }

}
