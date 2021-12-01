package com.eslam.csp1401_test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.IAuthenticationResult

import com.microsoft.identity.client.SilentAuthenticationCallback





class HomeFragment : Fragment() {
    val AUTHORITY = "https://login.microsoftonline.com/common"
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    private val scopes = listOf<String>("User.Read","Calendars.Read")
    var accessToken:String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PublicClientApplication.createSingleAccountPublicClientApplication(requireActivity().applicationContext,
            R.raw.auth_config_single_account, object : ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    mSingleAccountApp = application
                    loadAccount()
                }

                override fun onError(exception: MsalException) {
                    displayError(exception)
                }
            })
    }

    private fun loadAccount() {
        if (mSingleAccountApp == null) {
            return
        }
        mSingleAccountApp!!.getCurrentAccountAsync(object : CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                // You can use the account data to update your UI or your app database.
                if (activeAccount != null) {

                    updateUI(activeAccount.username)
                }

            }

            override fun onAccountChanged(
                priorAccount: IAccount?,
                currentAccount: IAccount?
            ) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    performOperationOnSignOut()
                }
            }

            override fun onError(exception: MsalException) {
                displayError(exception)
            }
        })
    }

    fun signIn()
    {
        if (mSingleAccountApp == null) {
            return;
        }
        getAuthInteractiveCallback()?.let {
            mSingleAccountApp!!.signIn(requireActivity(), null,
                scopes.toTypedArray(), it
            )
        };
    }


fun signOut()
{

    if (mSingleAccountApp == null) {
        return
    }
    mSingleAccountApp!!.signOut(object : SignOutCallback {
        override fun onSignOut() {
            updateUI("null")
            performOperationOnSignOut()
        }

        override fun onError( exception: MsalException) {
            displayError(exception)
        }
    })

}

    fun acquireTokenInteractive()
    {
        if (mSingleAccountApp == null) {
            return;
        }
        mSingleAccountApp!!.acquireToken(requireActivity(),
            scopes.toTypedArray(), getAuthInteractiveCallback()!!);
    }

    fun acquireTokenSilently()
    {
        if (mSingleAccountApp == null){
            return;
        }
        getAuthSilentCallback()?.let {
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
                /* Update UI */updateUI(authenticationResult.account.username)
                 accessToken = authenticationResult.accessToken;
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
               // Log.d(TAG, "Authentication failed: $exception")
                displayError(exception)
            }

            override fun onCancel() {
                /* User canceled the authentication */
                Log.d(null, "User cancelled login.")
            }
        }
    }

    private fun updateUI(account: String) {

    }

    private fun getAuthSilentCallback(): SilentAuthenticationCallback? {
        return object : SilentAuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                Log.d(null, "Successfully authenticated")
                accessToken = authenticationResult.accessToken
            }

            override fun onError(exception: MsalException) {
                Log.d(null, "Authentication failed: $exception")
                displayError(exception)
            }
        }
    }

    private fun displayError(exception: MsalException) {

        Toast.makeText(this.requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
    }

    private fun performOperationOnSignOut() {


    }

}
