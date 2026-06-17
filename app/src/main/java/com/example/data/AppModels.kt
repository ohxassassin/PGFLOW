package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val totalUnits: Int,
    val occupiedUnits: Int,
    val rentAmount: Double,
    val type: String, // "Apartment", "Single Family", "Townhouse"
    val imageUrl: String? = null
)

@Entity(tableName = "tenants")
data class Tenant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val propertyId: Int,
    val propertyName: String,
    val unitNumber: String,
    val leaseStart: String,
    val leaseEnd: String,
    val status: String // "Active", "Pending Onboarding", "Terminated"
)

@Entity(tableName = "payments")
data class RentPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tenantId: Int,
    val tenantName: String,
    val propertyId: Int,
    val propertyName: String,
    val amount: Double,
    val dueDate: String,
    val paidDate: String? = null,
    val status: String // "Paid", "Pending", "Overdue"
)

@Entity(tableName = "maintenance_requests")
data class MaintenanceRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val propertyId: Int,
    val propertyName: String,
    val unitNumber: String,
    val title: String,
    val description: String,
    val urgency: String, // "High" (Danger), "Medium" (Warning), "Low" (Normal)
    val status: String, // "Open", "In Progress", "Completed"
    val createdAt: String
)
