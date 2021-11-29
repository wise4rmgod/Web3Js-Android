package com.dev.web3js_android

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

class Mainviewmodel : ViewModel() {
    var web3: Web3j? = null

    //  var file: File? = null
    var Walletname: String? = null
    var credentials: Credentials? = null
    val getbalance: MutableLiveData<String> = MutableLiveData()

    //enter your own infura api key below
    init {
        web3 =
            Web3j.build(HttpService("https://rinkeby.infura.io/v3/e344xxxxxxxxxxxx872"))

    }


    fun connectToEthNetwork(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Now Connecting to Ethereum network", Toast.LENGTH_LONG).show()

                try {
                    //if the client version has an error the user will not gain access if successful the user will get connnected
                    val clientVersion: Web3ClientVersion? =
                        web3?.web3ClientVersion()?.sendAsync()?.get()
                    if (!clientVersion?.hasError()!!) {

                        Toast.makeText(context, "Connected!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, clientVersion.error.message, Toast.LENGTH_LONG).show()

                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()

                }

            }

        }

    }


    fun retrieveBalance( context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                //get wallet's balance
                try {
                    val balanceWei: EthGetBalance? = web3?.ethGetBalance(
                        credentials?.address,
                        DefaultBlockParameterName.LATEST
                    )?.sendAsync()
                        ?.get()
                  //  txtbalance?.text = "Your Balance" +
                            getbalance.postValue(balanceWei?.balance.toString()) //balanceWei?.balance
                } catch (e: Exception) {
                    Toast.makeText(context, "balance failed", Toast.LENGTH_LONG).show()
                }
            }

        }

    }

    fun createWallet(edtpassword: String, txtaddress: TextView?, file: File?, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                val password = edtpassword // this will be your etherium password
                try {
                    // generating the etherium wallet
                    Walletname = WalletUtils.generateLightNewWalletFile(password, file)
                    Toast.makeText(context, "Wallet generated wallet name is", Toast.LENGTH_LONG).show()

                    credentials =
                        WalletUtils.loadCredentials(password, file.toString() + "/" + Walletname)
                    txtaddress?.text = credentials?.address
                    //  ShowToast("success" +  credentials?.address)
                } catch (e: Exception) {
                    Toast.makeText(context, "failed", Toast.LENGTH_LONG).show()

                }
            }

        }

    }

    fun makeTransaction(edtvalue: Int, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                val value = edtvalue
                try {
                    val receipt: TransactionReceipt = Transfer.sendFunds(
                        web3,
                        credentials,
                        credentials?.address,
                        BigDecimal.valueOf(value.toLong()),
                        Convert.Unit.ETHER
                    ).send()
                    Toast.makeText(
                        context,
                        "Transaction successful: " + receipt.transactionHash,
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {

                    Log.d("low_balance", "low balance")
                }
            }

        }

    }

    //set up the security provider
    fun setupBouncyCastle() {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
                    ?: // Web3j will set up a provider  when it's used for the first time.
                    return@withContext
                if (provider.javaClass == BouncyCastleProvider::class.java) {
                    return@withContext
                }
                //There is a possibility  the bouncy castle registered by android may not have all ciphers
                //so we  substitute with the one bundled in the app.
                Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
                Security.insertProviderAt(BouncyCastleProvider(), 1)
            }

        }

    }

}