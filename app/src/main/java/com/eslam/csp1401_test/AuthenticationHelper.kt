package com.eslam.csp1401_test

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.microsoft.identity.client.exception.MsalUiRequiredException


object AuthenticationHelper {

    val AUTHORITY = "https://login.microsoftonline.com/common"
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    private val scopes = listOf<String>("User.Read","Calendars.ReadWrite")

    var exceptionState: MutableLiveData<MsalException?> = MutableLiveData()
    var signInState: MutableLiveData<Boolean> = MutableLiveData()
    var signOutState: MutableLiveData<Boolean> = MutableLiveData()
    var tokenState: MutableLiveData<String?> = MutableLiveData()



    fun initializeSingleAccountApp(context: Activity) {
        PublicClientApplication.createSingleAccountPublicClientApplication(context,
            R.raw.auth_config_single_account, object :
                IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    mSingleAccountApp = application
                    loadAccount(context)
                }

                override fun onError(exception: MsalException) {
                    //displayError(exception)
                    exceptionState.value =  exception
                }
            })
    }


    private fun loadAccount(activity: Activity) {
        if (mSingleAccountApp == null) {
            return
        }
        mSingleAccountApp!!.getCurrentAccountAsync(object :
            ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                // You can use the account data to update your UI or your app database.
                if (activeAccount != null) {
                    Log.e(null, "onAccountLoaded:${tokenState.value} ")

                    acquireTokenSilently(activity)

//                   if (accessToken != null)
//                {
//                    // updateUI(accessToken)
//                      }


                }

            }

            override fun onAccountChanged(
                priorAccount: IAccount?,
                currentAccount: IAccount?
            ) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    //performOperationOnSignOut()
                    signInState.value = false

                }
            }

            override fun onError(exception: MsalException) {
                //displayError(exception)

                Log.e("accountChanged", "onError: $exception ", )
            }
        })
    }


    fun signIn(signed:Boolean,activity: Activity)
    {
        if (mSingleAccountApp == null) {
            return;
        }
        if (signed)
        {

            acquireTokenSilently(activity)

        }else
        {
            getAuthInteractiveCallback()?.let {
                mSingleAccountApp!!.signIn(
                    activity, null,
                    scopes.toTypedArray(), it
                )

            }
        }

    }


    fun signOut()
    {


        if (mSingleAccountApp == null) {
            return
        }
        mSingleAccountApp!!.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                //updateUI("null")
                //performOperationOnSignOut()

                signOutState.value = true
            }

            override fun onError( exception: MsalException) {
                Log.e("signOut", "onError: $exception", )
                signOutState.value =  false
            }
        })


    }

    fun acquireTokenInteractive(activity:Activity)
    {
        if (mSingleAccountApp == null) {
            return;
        }
        mSingleAccountApp!!.acquireToken(activity,
            scopes.toTypedArray(), getAuthInteractiveCallback()!!);
    }

    fun acquireTokenSilently(activity: Activity)
    {
        if (mSingleAccountApp == null){
            return;
        }
        getAuthSilentCallback(activity)?.let {
            mSingleAccountApp!!.acquireTokenSilentAsync(scopes.toTypedArray(), AUTHORITY,
                it
            )
        }
    }

    private fun getAuthInteractiveCallback(): AuthenticationCallback? {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                //Log.d(TAG, "Successfully authenticated")
                tokenState.value = authenticationResult.accessToken;
               signInState.value = true

//                if (accessToken != null) {
//                    //updateUI(accessToken)
//                }
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                 Log.d(null, "Authentication failed: $exception")

               exceptionState.value = exception

            }

            override fun onCancel() {
                /* User canceled the authentication */
                Log.d(null, "User cancelled login.")
            }
        }
    }

    private fun getAuthSilentCallback(activity: Activity): SilentAuthenticationCallback? {
        return object : SilentAuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                Log.d(null, "Successfully authenticated")
                tokenState.value = authenticationResult.accessToken
                Log.e( null,"onSuccess: ${tokenState.value}", )
//                if (accessToken != null) {
//                    //updateUI(accessToken)
//                }
            }

            override fun onError(exception: MsalException) {
                Log.d(null, "Authentication failed: $exception")
               // displayError(exception)
                when (exception) {
                    is MsalClientException -> {
                        /* Exception inside MSAL, more info inside MsalError.java */
                    }
                    is MsalServiceException -> {
                        /* Exception when communicating with the STS, likely config issue */
                        exceptionState.value = exception
                    }
                    is MsalUiRequiredException -> {
                        /* Tokens expired or no session, retry with interactive */
                        acquireTokenInteractive(activity)
                    }
                }
            }
        }
    }




}

