package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(private val database: AppDatabase) {
    private val propertyDao = database.propertyDao()
    private val tenantDao = database.tenantDao()
    private val rentPaymentDao = database.rentPaymentDao()
    private val maintenanceRequestDao = database.maintenanceRequestDao()

    val allProperties: Flow<List<Property>> = propertyDao.getAllProperties()
    val allTenants: Flow<List<Tenant>> = tenantDao.getAllTenants()
    val allPayments: Flow<List<RentPayment>> = rentPaymentDao.getAllPayments()
    val allRequests: Flow<List<MaintenanceRequest>> = maintenanceRequestDao.getAllRequests()

    fun getPropertyById(id: Int): Flow<Property?> = propertyDao.getPropertyById(id)
    fun getTenantsByProperty(propertyId: Int): Flow<List<Tenant>> = tenantDao.getTenantsByProperty(propertyId)
    fun getPaymentsByProperty(propertyId: Int): Flow<List<RentPayment>> = rentPaymentDao.getPaymentsByProperty(propertyId)
    fun getRequestsByProperty(propertyId: Int): Flow<List<MaintenanceRequest>> = maintenanceRequestDao.getRequestsByProperty(propertyId)

    suspend fun insertProperty(property: Property) = propertyDao.insertProperty(property)
    suspend fun deleteProperty(property: Property) = propertyDao.deleteProperty(property)

    suspend fun insertTenant(tenant: Tenant) = tenantDao.insertTenant(tenant)
    suspend fun deleteTenant(tenant: Tenant) = tenantDao.deleteTenant(tenant)

    suspend fun insertPayment(payment: RentPayment) = rentPaymentDao.insertPayment(payment)
    suspend fun updatePaymentStatus(paymentId: Int, status: String, paidDate: String?) =
        rentPaymentDao.updatePaymentStatus(paymentId, status, paidDate)
    suspend fun deletePayment(payment: RentPayment) = rentPaymentDao.deletePayment(payment)

    suspend fun insertRequest(request: MaintenanceRequest) = maintenanceRequestDao.insertRequest(request)
    suspend fun updateRequestStatus(requestId: Int, status: String) =
        maintenanceRequestDao.updateRequestStatus(requestId, status)
    suspend fun deleteRequest(request: MaintenanceRequest) = maintenanceRequestDao.deleteRequest(request)

    /**
     * Checks if the database consists of any data. If not, seeds it with rich, PGFlow premium demo properties,
     * tenants, payments, and maintenance logs.
     */
    suspend fun seedDatabaseIfEmpty() {
        val existingProperties = allProperties.first()
        if (existingProperties.isEmpty()) {
            // 1. Seed properties
            val p1 = Property(
                name = "Oakridge heights",
                address = "405 Laurel Dr, Portland, OR",
                totalUnits = 12,
                occupiedUnits = 10,
                rentAmount = 1850.0,
                type = "Apartment",
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=500&auto=format&fit=crop&q=60"
            )
            val p2 = Property(
                name = "The Aspen Townhomes",
                address = "1932 Pine Needle Ln, Denver, CO",
                totalUnits = 6,
                occupiedUnits = 5,
                rentAmount = 2450.0,
                type = "Townhouse",
                imageUrl = "https://images.unsplash.com/photo-1605276374104-dee2a0ed3cd6?w=500&auto=format&fit=crop&q=60"
            )
            val p3 = Property(
                name = "Pacific Plaza Commercial",
                address = "800 Bellevue Way NE, Bellevue, WA",
                totalUnits = 4,
                occupiedUnits = 3,
                rentAmount = 4200.0,
                type = "Commercial",
                imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=500&auto=format&fit=crop&q=60"
            )
            val p4 = Property(
                name = "Summit Ridge Lodge",
                address = "338 Whistler Way, Park City, UT",
                totalUnits = 8,
                occupiedUnits = 8,
                rentAmount = 3100.0,
                type = "Single Family",
                imageUrl = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=500&auto=format&fit=crop&q=60"
            )

            // Insert into DB and we can retrieve auto-gen IDs or just let them auto-increment
            // First property gets id=1, second gets id=2, etc. (since autoincrement starts at 1)
            propertyDao.insertProperty(p1)
            propertyDao.insertProperty(p2)
            propertyDao.insertProperty(p3)
            propertyDao.insertProperty(p4)

            // 2. Seed tenants
            tenantDao.insertTenant(Tenant(
                name = "John Doe",
                email = "john.doe@gmail.com",
                phone = "503-555-0143",
                propertyId = 1,
                propertyName = "Oakridge heights",
                unitNumber = "Apt 101",
                leaseStart = "2025-06-01",
                leaseEnd = "2026-05-31",
                status = "Active"
            ))
            tenantDao.insertTenant(Tenant(
                name = "Jane Smith",
                email = "jane.smith@yahoo.com",
                phone = "303-555-0982",
                propertyId = 2,
                propertyName = "The Aspen Townhomes",
                unitNumber = "Townhouse #3",
                leaseStart = "2026-01-15",
                leaseEnd = "2027-01-14",
                status = "Active"
            ))
            tenantDao.insertTenant(Tenant(
                name = "Mike Johnson",
                email = "mike.j@pacificcorp.com",
                phone = "425-555-4422",
                propertyId = 3,
                propertyName = "Pacific Plaza Commercial",
                unitNumber = "Suite 410",
                leaseStart = "2024-09-01",
                leaseEnd = "2029-08-31",
                status = "Active"
            ))
            tenantDao.insertTenant(Tenant(
                name = "Sarah Lin",
                email = "sarah.lin@gmail.com",
                phone = "206-555-8833",
                propertyId = 1,
                propertyName = "Oakridge heights",
                unitNumber = "Apt 204",
                leaseStart = "2026-05-01",
                leaseEnd = "2027-04-30",
                status = "Active"
            ))
            tenantDao.insertTenant(Tenant(
                name = "Amir Patel",
                email = "amir.patel@gmail.com",
                phone = "801-555-7711",
                propertyId = 4,
                propertyName = "Summit Ridge Lodge",
                unitNumber = "Main Cabin",
                leaseStart = "2026-06-01",
                leaseEnd = "2027-05-31",
                status = "Active"
            ))
            tenantDao.insertTenant(Tenant(
                name = "Clara Vane",
                email = "clara.v@outlook.com",
                phone = "512-555-2244",
                propertyId = 1,
                propertyName = "Oakridge heights",
                unitNumber = "Apt 305",
                leaseStart = "2026-07-01",
                leaseEnd = "2027-06-30",
                status = "Pending Onboarding"
            ))

            // 3. Seed payments
            rentPaymentDao.insertPayment(RentPayment(
                tenantId = 1,
                tenantName = "John Doe",
                propertyId = 1,
                propertyName = "Oakridge heights",
                amount = 1850.0,
                dueDate = "2026-06-01",
                paidDate = "2026-06-02",
                status = "Paid"
            ))
            rentPaymentDao.insertPayment(RentPayment(
                tenantId = 2,
                tenantName = "Jane Smith",
                propertyId = 2,
                propertyName = "The Aspen Townhomes",
                amount = 2450.0,
                dueDate = "2026-06-15",
                paidDate = null,
                status = "Pending"
            ))
            rentPaymentDao.insertPayment(RentPayment(
                tenantId = 3,
                tenantName = "Mike Johnson",
                propertyId = 3,
                propertyName = "Pacific Plaza Commercial",
                amount = 4200.0,
                dueDate = "2026-06-01",
                paidDate = "2026-05-29",
                status = "Paid"
            ))
            rentPaymentDao.insertPayment(RentPayment(
                tenantId = 4,
                tenantName = "Sarah Lin",
                propertyId = 1,
                propertyName = "Oakridge heights",
                amount = 1850.0,
                dueDate = "2026-05-01",
                paidDate = null,
                status = "Overdue"
            ))
            rentPaymentDao.insertPayment(RentPayment(
                tenantId = 5,
                tenantName = "Amir Patel",
                propertyId = 4,
                propertyName = "Summit Ridge Lodge",
                amount = 3100.0,
                dueDate = "2026-06-01",
                paidDate = "2026-06-01",
                status = "Paid"
            ))

            // 4. Seed maintenance requests
            maintenanceRequestDao.insertRequest(MaintenanceRequest(
                propertyId = 1,
                propertyName = "Oakridge heights",
                unitNumber = "Apt 204",
                title = "Clogged Garbage Disposal",
                description = "Kitchen sink has standing water and garbage disposal hums but does not spin. Might have food jammed.",
                urgency = "Medium",
                status = "Open",
                createdAt = "2026-06-14"
            ))
            maintenanceRequestDao.insertRequest(MaintenanceRequest(
                propertyId = 2,
                propertyName = "The Aspen Townhomes",
                unitNumber = "Townhouse #3",
                title = "Leaky Roof in Master Bedroom",
                description = "Notice a water stain forming on the master bedroom ceiling. It drips slowly during heavy rainfall.",
                urgency = "High",
                status = "In Progress",
                createdAt = "2026-06-12"
            ))
            maintenanceRequestDao.insertRequest(MaintenanceRequest(
                propertyId = 3,
                propertyName = "Pacific Plaza Commercial",
                unitNumber = "Suite 410",
                title = "Conference Room AC Broken",
                description = "Thermostat is set to 70 but room is hovering at 78 degrees. Tenants complained it is too warm during meetings.",
                urgency = "High",
                status = "Open",
                createdAt = "2026-06-16"
            ))
            maintenanceRequestDao.insertRequest(MaintenanceRequest(
                propertyId = 4,
                propertyName = "Summit Ridge Lodge",
                unitNumber = "Main Cabin",
                title = "Squeaky Front Door Hinges",
                description = "Front entry door squeaks very loudly when opening or closing. Just needs some WD-40 or lubrication.",
                urgency = "Low",
                status = "Completed",
                createdAt = "2026-06-10"
            ))
        }
    }
}
