package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    // Session states
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _userPhone = MutableStateFlow<String?>(null)
    val userPhone: StateFlow<String?> = _userPhone.asStateFlow()

    private val _preferredCurrency = MutableStateFlow("USD")
    val preferredCurrency: StateFlow<String> = _preferredCurrency.asStateFlow()

    fun login(email: String, phone: String, currency: String) {
        _userEmail.value = email.ifBlank { "guest@pgflow.com" }
        _userPhone.value = phone.ifBlank { "+919999999999" }
        _preferredCurrency.value = currency
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
        _userEmail.value = null
        _userPhone.value = null
    }

    fun updateCurrency(currency: String) {
        _preferredCurrency.value = currency
    }

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    val properties: StateFlow<List<Property>> = repository.allProperties
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tenants: StateFlow<List<Tenant>> = repository.allTenants
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val payments: StateFlow<List<RentPayment>> = repository.allPayments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val maintenanceRequests: StateFlow<List<MaintenanceRequest>> = repository.allRequests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Form selection helpers
    fun getTenantById(tenantId: Int): Tenant? {
        return tenants.value.find { it.id == tenantId }
    }

    fun getPropertyById(propertyId: Int): Property? {
        return properties.value.find { it.id == propertyId }
    }

    // Actions
    fun addProperty(
        name: String,
        address: String,
        totalUnits: Int,
        occupiedUnits: Int,
        rentAmount: Double,
        type: String,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            repository.insertProperty(
                Property(
                    name = name,
                    address = address,
                    totalUnits = totalUnits,
                    occupiedUnits = occupiedUnits,
                    rentAmount = rentAmount,
                    type = type,
                    imageUrl = imageUrl ?: "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=500&auto=format&fit=crop&q=60"
                )
            )
        }
    }

    fun addTenant(
        name: String,
        email: String,
        phone: String,
        propertyId: Int,
        unitNumber: String,
        leaseStart: String,
        leaseEnd: String,
        status: String
    ) {
        viewModelScope.launch {
            val property = properties.value.find { it.id == propertyId }
            val propName = property?.name ?: "Unknown Property"
            
            // Insert the tenant
            repository.insertTenant(
                Tenant(
                    name = name,
                    email = email,
                    phone = phone,
                    propertyId = propertyId,
                    propertyName = propName,
                    unitNumber = unitNumber,
                    leaseStart = leaseStart,
                    leaseEnd = leaseEnd,
                    status = status
                )
            )

            // If tenant is active, increment occupiedUnits of the property
            if (property != null && status == "Active") {
                val updatedOccupied = (property.occupiedUnits + 1).coerceAtMost(property.totalUnits)
                repository.insertProperty(property.copy(occupiedUnits = updatedOccupied))
            }
        }
    }

    fun addPayment(
        tenantId: Int,
        amount: Double,
        dueDate: String,
        status: String
    ) {
        viewModelScope.launch {
            val tenant = tenants.value.find { it.id == tenantId } ?: return@launch
            repository.insertPayment(
                RentPayment(
                    tenantId = tenantId,
                    tenantName = tenant.name,
                    propertyId = tenant.propertyId,
                    propertyName = tenant.propertyName,
                    amount = amount,
                    dueDate = dueDate,
                    paidDate = if (status == "Paid") getCurrentDateString() else null,
                    status = status
                )
            )
        }
    }

    fun togglePaymentStatus(payment: RentPayment) {
        viewModelScope.launch {
            val newStatus = if (payment.status == "Paid") "Overdue" else "Paid"
            val paidDate = if (newStatus == "Paid") getCurrentDateString() else null
            repository.updatePaymentStatus(payment.id, newStatus, paidDate)
        }
    }

    fun deletePayment(payment: RentPayment) {
        viewModelScope.launch {
            repository.deletePayment(payment)
        }
    }

    fun addMaintenanceRequest(
        propertyId: Int,
        unitNumber: String,
        title: String,
        description: String,
        urgency: String
    ) {
        viewModelScope.launch {
            val property = properties.value.find { it.id == propertyId }
            val propName = property?.name ?: "Unknown Property"

            repository.insertRequest(
                MaintenanceRequest(
                    propertyId = propertyId,
                    propertyName = propName,
                    unitNumber = unitNumber,
                    title = title,
                    description = description,
                    urgency = urgency,
                    status = "Open",
                    createdAt = getCurrentDateString()
                )
            )
        }
    }

    fun updateRequestStatus(requestId: Int, status: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, status)
        }
    }

    fun deleteRequest(request: MaintenanceRequest) {
        viewModelScope.launch {
            repository.deleteRequest(request)
        }
    }

    private fun getCurrentDateString(): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date())
        } catch (e: Exception) {
            "2026-06-17"
        }
    }
}

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val repository = AppRepository(database)
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
