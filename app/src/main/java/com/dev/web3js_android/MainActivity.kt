package com.dev.web3js_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.io.File
import java.lang.Exception
import java.math.BigDecimal
import java.security.Security

class MainActivity : AppCompatActivity() {
    var web3: Web3j? = null
    var file: File? = null
    var Walletname: String? = null
    var credentials: Credentials? = null
    var txtaddress: TextView? = null
    val main: Mainviewmodel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtaddress = findViewById(R.id.text_address)

        //  this is the pathname for the file that will be created and stores the wallet details
        val Edtpath = findViewById<EditText>(R.id.walletpath)
        val etheriumwalletPath = Edtpath.text.toString()
        val Edtpassword = findViewById<EditText>(R.id.password)
        val txtbalance = findViewById<TextView>(R.id.text_balance)
        val btncnt = findViewById<Button>(R.id.buttonsconnect)
        val btnblnc = findViewById<Button>(R.id.buttonbalance)
        val creatbtnb = findViewById<Button>(R.id.create_button)
        val sendethbtn = findViewById<Button>(R.id.sendEth)
        val Edtvalue = findViewById<EditText>(R.id.ethvalue)

        file = File(filesDir.toString() + etheriumwalletPath) // the etherium wallet location

        //create the directory if it does not exist
        if (!file!!.mkdirs()) {
            file!!.mkdirs()
        } else {
            Toast.makeText(
                applicationContext, "Directory already created",
                Toast.LENGTH_LONG
            ).show()
        }

        main.setupBouncyCastle()
        main.getbalance.observe(this, Observer {
            txtbalance.text = it
        })
        btncnt.setOnClickListener {
            // connect to the etheruem network
            main.connectToEthNetwork(this)
        }
        btnblnc.setOnClickListener {
            main.retrieveBalance(this)
        }
        creatbtnb.setOnClickListener {
            // Create wallet
            main.createWallet(Edtpassword.text.toString(), txtaddress, file, this)
        }
        sendethbtn.setOnClickListener {
            main.makeTransaction(Edtvalue.text.toString().toInt(), this)
        }

    }

}