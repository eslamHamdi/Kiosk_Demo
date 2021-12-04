package com.eslam.csp1401_test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.get
import com.eslam.csp1401_test.databinding.FragmentHomeBinding
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.IAuthenticationResult

import com.microsoft.identity.client.SilentAuthenticationCallback
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalServiceException
import com.microsoft.identity.client.exception.MsalUiRequiredException


class HomeFragment : Fragment() {

    val viewModel:MainViewModel by activityViewModels()
    lateinit var binding:FragmentHomeBinding
    val authHelper:AuthenticationHelper = AuthenticationHelper
    var signed = false












    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding =  FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authHelper.initializeSingleAccountApp(this.requireActivity())

        binding.signIn.setOnClickListener {

          authHelper.signIn(signed,this.requireActivity())
        }

        authHelper.exceptionState.observe(viewLifecycleOwner){
                displayError(it)

        }

        authHelper.tokenState.observe(viewLifecycleOwner){
            Log.e("tokenState", "onViewCreated: $it", )
            updateUI(it)
        }

        authHelper.signInState.observe(viewLifecycleOwner){
            signed = it
        }


    }


    private fun updateUI(token: String?) {

        //acquireTokenSilently()
        val controller = findNavController()

        Log.e(null, "updateUI: $token ", )
        controller.safeNavigate(HomeFragmentDirections.actionHomeFragmentToEventsFragment(token))
       // val dest =controller.currentDestination

//                if(controller.currentDestination == controller.graph[R.id.homeFragment]){
//                    controller.navigate(HomeFragmentDirections.actionHomeFragmentToEventsFragment(token))
//                    controller.currentDestination
//                }



    }


    private fun displayError(exception: MsalException?) {

        if (exception != null) {
            Toast.makeText(this.requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }

    private fun performOperationOnSignOut() {


    }

    override fun onResume() {
        super.onResume()
        //loadAccount()
    }
    fun NavController.safeNavigate(direction: NavDirections) {

        currentDestination?.getAction(direction.actionId)?.run {

            navigate(direction)
        }
    }
}
