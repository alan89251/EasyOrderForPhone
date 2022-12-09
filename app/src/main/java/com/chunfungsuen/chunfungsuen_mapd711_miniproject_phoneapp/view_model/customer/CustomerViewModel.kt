package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.view_model.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerModel
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.data_model.customer.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerViewModel(
    // dependency injection of customerRepository
    private val customerRepository: CustomerRepository
)
    : ViewModel()
{
    var isLoadedCustomer = false // indicate whether loaded the customer info or not
        private set
    var customer: LiveData<CustomerModel>? = null
        get() {
            if (!isLoadedCustomer) {
                // if user do not login or sign up, there is no info of the current customer
                throw IllegalStateException("customer is not ready")
            }
            return field
        }
        private set

    /**
     * load the customer from repository and set the live data of it
     */
    fun loadCustomer(userName: String) {
        customer = customerRepository.getCustomer(userName)
        isLoadedCustomer = true
    }

    /**
     * insert the current customer to the repository
     * After that, load the inserted customer and update the live object
     */
    fun insertCustomer(customer: CustomerModel) {
        CoroutineScope(Dispatchers.IO).launch {
            customerRepository.insert(customer)

            withContext(Dispatchers.Main) {
                // load the inserted customer and update the live object
                loadCustomer(customer.UserName)
            }
        }
    }

    /**
     * save the update of the customer info to the repository
     * after that, call the callback "onUpdated"
     */
    fun updateCustomer(updatedCustomer: CustomerModel, onUpdated: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            customerRepository.updateCustomer(updatedCustomer)

            withContext(Dispatchers.Main) {
                onUpdated() // run instructions after updated
            }
        }
    }
}