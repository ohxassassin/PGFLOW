package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties ORDER BY name ASC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE id = :id LIMIT 1")
    fun getPropertyById(id: Int): Flow<Property?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property)

    @Delete
    suspend fun deleteProperty(property: Property)
}

@Dao
interface TenantDao {
    @Query("SELECT * FROM tenants ORDER BY name ASC")
    fun getAllTenants(): Flow<List<Tenant>>

    @Query("SELECT * FROM tenants WHERE propertyId = :propertyId")
    fun getTenantsByProperty(propertyId: Int): Flow<List<Tenant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: Tenant)

    @Delete
    suspend fun deleteTenant(tenant: Tenant)
}

@Dao
interface RentPaymentDao {
    @Query("SELECT * FROM payments ORDER BY dueDate DESC")
    fun getAllPayments(): Flow<List<RentPayment>>

    @Query("SELECT * FROM payments WHERE propertyId = :propertyId ORDER BY dueDate DESC")
    fun getPaymentsByProperty(propertyId: Int): Flow<List<RentPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: RentPayment)

    @Query("UPDATE payments SET status = :status, paidDate = :paidDate WHERE id = :paymentId")
    suspend fun updatePaymentStatus(paymentId: Int, status: String, paidDate: String?)

    @Delete
    suspend fun deletePayment(payment: RentPayment)
}

@Dao
interface MaintenanceRequestDao {
    @Query("SELECT * FROM maintenance_requests ORDER BY id DESC")
    fun getAllRequests(): Flow<List<MaintenanceRequest>>

    @Query("SELECT * FROM maintenance_requests WHERE propertyId = :propertyId ORDER BY id DESC")
    fun getRequestsByProperty(propertyId: Int): Flow<List<MaintenanceRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: MaintenanceRequest)

    @Query("UPDATE maintenance_requests SET status = :status WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Int, status: String)

    @Delete
    suspend fun deleteRequest(request: MaintenanceRequest)
}
