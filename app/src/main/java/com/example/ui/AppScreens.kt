package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.theme.*
import java.text.DecimalFormat

// Context-aware currency formatters for geometric balance
fun formatCurrency(amount: Double, currencyCode: String): String {
    val symbol = when (currencyCode.uppercase()) {
        "INR" -> "₹"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        else -> "$"
    }
    val formatter = DecimalFormat("#,##0")
    return "$symbol${formatter.format(amount)}"
}

fun getCurrencySymbol(currencyCode: String): String {
    return when (currencyCode.uppercase()) {
        "INR" -> "₹"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        else -> "$"
    }
}

@Composable
fun StatusPill(text: String, severity: String) {
    val (bgColor, textColor) = when (severity.lowercase()) {
        "success", "paid", "active", "completed" -> Pair(SuccessGreenBackground, SuccessGreen)
        "warning", "pending", "pending onboarding", "in progress" -> Pair(WarningAmberBackground, WarningAmber)
        "danger", "overdue", "high", "failed" -> Pair(DangerRedBackground, DangerRed)
        else -> Pair(Color(0xFFEEF2F6), Color(0xFF64748B)) // Neutral/Low priority
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}

data class CountryInfo(val code: String, val name: String, val flag: String)

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, phone: String, currency: String) -> Unit
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("USD") } // USD by default

    val countries = listOf(
        CountryInfo("+91", "India", "🇮🇳"),
        CountryInfo("+1", "United States", "🇺🇸"),
        CountryInfo("+44", "United Kingdom", "🇬🇧"),
        CountryInfo("+33", "France", "🇫🇷"),
        CountryInfo("+49", "Germany", "🇩🇪"),
        CountryInfo("+61", "Australia", "🇦🇺"),
        CountryInfo("+81", "Japan", "🇯🇵"),
        CountryInfo("+1", "Canada", "🇨🇦"),
        CountryInfo("+65", "Singapore", "🇸🇬"),
        CountryInfo("+55", "Brazil", "🇧🇷"),
        CountryInfo("+971", "UAE", "🇦🇪")
    )
    var selectedCountry by remember { mutableStateOf(countries.first()) }
    var countryDropdownExpanded by remember { mutableStateOf(false) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    val currencies = listOf("USD", "INR", "EUR", "GBP")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteCanvas)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(PureWhiteCard)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant geometric header icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryIndigo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "PGFlow",
                    tint = ActionBlue,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PGFlow Portfolio",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = TextColorDark,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = if (isSignUpMode) "Create an account to begin managing" else "Welcome back, manage your properties",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextColorMuted,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Modes button toggles selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ActionBlueContainer)
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isSignUpMode) ActionBlue else Color.Transparent)
                        .clickable { isSignUpMode = false }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sign In",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (!isSignUpMode) PrimaryIndigoContainer else TextColorMuted,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSignUpMode) ActionBlue else Color.Transparent)
                        .clickable { isSignUpMode = true }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Sign Up",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (isSignUpMode) PrimaryIndigoContainer else TextColorMuted,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Name field for sign up
            if (isSignUpMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = TextColorMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = OffWhiteCanvas,
                        unfocusedContainerColor = OffWhiteCanvas,
                        focusedBorderColor = ActionBlue,
                        unfocusedBorderColor = OutlineColor
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Email Address
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = TextColorMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = OffWhiteCanvas,
                    unfocusedContainerColor = OffWhiteCanvas,
                    focusedBorderColor = ActionBlue,
                    unfocusedBorderColor = OutlineColor
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone state country dialing selection block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country dial code trigger select button
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OffWhiteCanvas)
                        .border(1.dp, OutlineColor, RoundedCornerShape(12.dp))
                        .clickable { countryDropdownExpanded = true }
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "${selectedCountry.flag} ${selectedCountry.code}", style = MaterialTheme.typography.bodyMedium.copy(color = TextColorDark, fontWeight = FontWeight.Bold))
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Country dropdown", tint = TextColorMuted, modifier = Modifier.size(16.dp))
                    }

                    DropdownMenu(
                        expanded = countryDropdownExpanded,
                        onDismissRequest = { countryDropdownExpanded = false },
                        modifier = Modifier
                            .background(PureWhiteCard)
                            .heightIn(max = 240.dp)
                    ) {
                        countries.forEach { item ->
                            DropdownMenuItem(
                                text = { Text("${item.flag} ${item.name} (${item.code})", color = TextColorDark) },
                                onClick = {
                                    selectedCountry = item
                                    countryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Phone number text input
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .weight(0.55f)
                        .testTag("auth_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = OffWhiteCanvas,
                        unfocusedContainerColor = OffWhiteCanvas,
                        focusedBorderColor = ActionBlue,
                        unfocusedBorderColor = OutlineColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Currency Option Selector Dropdown Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OffWhiteCanvas)
                    .border(1.dp, OutlineColor, RoundedCornerShape(12.dp))
                    .clickable { currencyDropdownExpanded = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Business Currency", style = MaterialTheme.typography.labelSmall.copy(color = TextColorMuted))
                        val labelText = when (selectedCurrency) {
                            "INR" -> "Indian Rupees (INR - ₹)"
                            "USD" -> "US Dollar (USD - $)"
                            "EUR" -> "Euro (EUR - €)"
                            "GBP" -> "British Pound (GBP - £)"
                            else -> selectedCurrency
                        }
                        Text(labelText, style = MaterialTheme.typography.bodyMedium.copy(color = TextColorDark, fontWeight = FontWeight.SemiBold))
                    }
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Currency code picker", tint = TextColorMuted)
                }

                DropdownMenu(
                    expanded = currencyDropdownExpanded,
                    onDismissRequest = { currencyDropdownExpanded = false },
                    modifier = Modifier
                        .background(PureWhiteCard)
                        .fillMaxWidth(0.8f)
                ) {
                    currencies.forEach { item ->
                        val codeText = when (item) {
                            "INR" -> "Indian Rupees (INR - ₹)"
                            "USD" -> "US Dollar (USD - $)"
                            "EUR" -> "Euro (EUR - €)"
                            "GBP" -> "British Pound (GBP - £)"
                            else -> item
                        }
                        DropdownMenuItem(
                            text = { Text(codeText, color = if (selectedCurrency == item) ActionBlue else TextColorDark) },
                            onClick = {
                                selectedCurrency = item
                                currencyDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary auth submit button
            Button(
                onClick = {
                    val finalEmail = if (email.isBlank()) "guest@pgflow.com" else email
                    val finalPhone = "${selectedCountry.code} ${phone.ifBlank { "9999999999" }}"
                    onLoginSuccess(finalEmail, finalPhone, selectedCurrency)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_submit_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ActionBlue,
                    contentColor = PrimaryIndigoContainer
                )
            ) {
                Text(
                    text = if (isSignUpMode) "Register & Start Management" else "Login to Dashboard",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Visual social divider line
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineColor)
                Text(
                    "or continue with",
                    modifier = Modifier.padding(horizontal = 10.dp),
                    style = MaterialTheme.typography.labelSmall.copy(color = TextColorMuted)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineColor)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social platforms buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Continue with Google Button
                OutlinedButton(
                    onClick = {
                        onLoginSuccess("google.user@gmail.com", "${selectedCountry.code} 8888888888", selectedCurrency)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("auth_google_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColorDark),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google", style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp))
                    }
                }

                // Continue with Apple ID Button
                OutlinedButton(
                    onClick = {
                        onLoginSuccess("apple.account@icloud.com", "${selectedCountry.code} 7777777777", selectedCurrency)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("auth_apple_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextColorDark),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("", style = MaterialTheme.typography.labelSmall.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apple ID", style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp))
                    }
                }
            }
        }
    }
}

@Composable
fun PGFlowMainScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val preferredCurrency by viewModel.preferredCurrency.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { email, phone, currency ->
                viewModel.login(email, phone, currency)
            }
        )
    } else {
        var selectedTab by remember { mutableStateOf(0) }

        // State flows from ViewModel
        val properties by viewModel.properties.collectAsState()
        val tenants by viewModel.tenants.collectAsState()
        val payments by viewModel.payments.collectAsState()
        val maintenanceRequests by viewModel.maintenanceRequests.collectAsState()

        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = PureWhiteCard,
                    tonalElevation = 4.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(if (selectedTab == 0) Icons.Default.GridView else Icons.Default.GridView, contentDescription = "Dashboard") },
                        label = { Text("Overview") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ActionBlue,
                            selectedTextColor = ActionBlue,
                            unselectedIconColor = TextColorMuted,
                            unselectedTextColor = TextColorMuted,
                            indicatorColor = ActionBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_dashboard_tab")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(if (selectedTab == 1) Icons.Default.Business else Icons.Outlined.Business, contentDescription = "Properties") },
                        label = { Text("Properties") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ActionBlue,
                            selectedTextColor = ActionBlue,
                            unselectedIconColor = TextColorMuted,
                            unselectedTextColor = TextColorMuted,
                            indicatorColor = ActionBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_properties_tab")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(if (selectedTab == 2) Icons.Default.People else Icons.Outlined.People, contentDescription = "Tenants") },
                        label = { Text("Tenants") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ActionBlue,
                            selectedTextColor = ActionBlue,
                            unselectedIconColor = TextColorMuted,
                            unselectedTextColor = TextColorMuted,
                            indicatorColor = ActionBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_tenants_tab")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(if (selectedTab == 3) Icons.Default.Assignment else Icons.Outlined.Assignment, contentDescription = "Operations") },
                        label = { Text("Operations") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ActionBlue,
                            selectedTextColor = ActionBlue,
                            unselectedIconColor = TextColorMuted,
                            unselectedTextColor = TextColorMuted,
                            indicatorColor = ActionBlueContainer
                        ),
                        modifier = Modifier.testTag("nav_operations_tab")
                    )
                }
            },
            contentWindowInsets = WindowInsets.navigationBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(OffWhiteCanvas)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "screen_transition"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> DashboardScreen(
                            properties = properties,
                            tenants = tenants,
                            payments = payments,
                            maintenanceRequests = maintenanceRequests,
                            preferredCurrency = preferredCurrency,
                            userEmail = userEmail,
                            userPhone = userPhone,
                            onUpdateCurrency = { viewModel.updateCurrency(it) },
                            onLogout = { viewModel.logout() },
                            onNavigateToRent = { selectedTab = 3 },
                            onNavigateToMaintenance = { selectedTab = 3 },
                            onNavigateToTenants = { selectedTab = 2 },
                            onNavigateToProperties = { selectedTab = 1 }
                        )
                        1 -> PropertiesScreen(
                            properties = properties,
                            preferredCurrency = preferredCurrency,
                            onAddProperty = { name, address, units, rent, type ->
                                viewModel.addProperty(name, address, units, 0, rent, type)
                            }
                        )
                        2 -> TenantsScreen(
                            tenants = tenants,
                            properties = properties,
                            onAddTenant = { name, email, phone, propId, unitNo, start, end, status ->
                                viewModel.addTenant(name, email, phone, propId, unitNo, start, end, status)
                            }
                        )
                        3 -> OperationsScreen(
                            payments = payments,
                            maintenanceRequests = maintenanceRequests,
                            properties = properties,
                            tenants = tenants,
                            preferredCurrency = preferredCurrency,
                            onTogglePayment = { viewModel.togglePaymentStatus(it) },
                            onAddPayment = { tenantId, amount, due, status ->
                                viewModel.addPayment(tenantId, amount, due, status)
                            },
                            onAddRequest = { propId, unitNo, title, desc, urgency ->
                                viewModel.addMaintenanceRequest(propId, unitNo, title, desc, urgency)
                            },
                            onUpdateRequestStatus = { reqId, status ->
                                viewModel.updateRequestStatus(reqId, status)
                            }
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: DASHBOARD OVERVIEW SCREEN
// -------------------------------------------------------------
@Composable
fun DashboardScreen(
    properties: List<Property>,
    tenants: List<Tenant>,
    payments: List<RentPayment>,
    maintenanceRequests: List<MaintenanceRequest>,
    preferredCurrency: String,
    userEmail: String?,
    userPhone: String?,
    onUpdateCurrency: (String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToRent: () -> Unit,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToTenants: () -> Unit,
    onNavigateToProperties: () -> Unit
) {
    // Computations
    val activeTenantsCount = tenants.count { it.status == "Active" }
    val totalUnits = properties.sumOf { it.totalUnits }
    val occupiedUnits = properties.sumOf { it.occupiedUnits }
    val occupancyRate = if (totalUnits > 0) (occupiedUnits.toFloat() / totalUnits * 100).toInt() else 0

    val unpaidRevenue = payments.filter { it.status != "Paid" }.sumOf { it.amount }
    val collectedRevenue = payments.filter { it.status == "Paid" }.sumOf { it.amount }
    val openMaintenanceCount = maintenanceRequests.count { it.status != "Completed" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // App Premium Header Card with Subtle Dynamic Gradient Accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryIndigo, PrimaryIndigoContainer)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = "Logo",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PGFlow",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                            )
                            Text(
                                text = "PORTFOLIO INTELLIGENCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ActionBlueContainer,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                    }

                    // User Profile menu
                    var userMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = { userMenuExpanded = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ActionBlue)
                                .size(36.dp)
                        ) {
                            Text(
                                text = (userEmail ?: "G").take(1).uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = PrimaryIndigoContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = userMenuExpanded,
                            onDismissRequest = { userMenuExpanded = false },
                            modifier = Modifier.background(PureWhiteCard)
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text("User Account", style = MaterialTheme.typography.labelSmall.copy(color = TextColorMuted))
                                        Text(userEmail ?: "guest@pgflow.com", style = MaterialTheme.typography.bodyMedium.copy(color = TextColorDark, fontWeight = FontWeight.Bold))
                                        if (!userPhone.isNullOrBlank()) {
                                            Text(userPhone, style = MaterialTheme.typography.bodySmall.copy(color = TextColorMuted))
                                        }
                                    }
                                },
                                onClick = {},
                                enabled = false
                            )
                            HorizontalDivider(color = OutlineColor)
                            DropdownMenuItem(
                                text = { Text("Currency: USD ($)", color = if (preferredCurrency == "USD") ActionBlue else TextColorDark) },
                                onClick = { 
                                    onUpdateCurrency("USD")
                                    userMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Currency: INR (₹)", color = if (preferredCurrency == "INR") ActionBlue else TextColorDark) },
                                onClick = { 
                                    onUpdateCurrency("INR")
                                    userMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Currency: EUR (€)", color = if (preferredCurrency == "EUR") ActionBlue else TextColorDark) },
                                onClick = { 
                                    onUpdateCurrency("EUR")
                                    userMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Currency: GBP (£)", color = if (preferredCurrency == "GBP") ActionBlue else TextColorDark) },
                                onClick = { 
                                    onUpdateCurrency("GBP")
                                    userMenuExpanded = false
                                }
                            )
                            HorizontalDivider(color = OutlineColor)
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out", tint = DangerRed, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Log Out", color = DangerRed)
                                    }
                                },
                                onClick = { 
                                    userMenuExpanded = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Hero KPI Card nestled in of the Indigo background
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_metric_card"),
                    colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "REVENUE COLLECTED THIS MONTH",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = TextColorMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatCurrency(collectedRevenue, preferredCurrency),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = ActionBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = OutlineColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Outstanding Rent",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted)
                                )
                                Text(
                                    text = formatCurrency(unpaidRevenue, preferredCurrency),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = DangerRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Portfolio Occupancy",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted)
                                )
                                Text(
                                    text = "$occupancyRate% ($occupiedUnits/$totalUnits)",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION: Core Quick Stats Cards Grid
        PaddingScope {
            Text(
                text = "PORTFOLIO ACTION SUMMARY",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextColorMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // stat card 1
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTenants() },
                    colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ActionBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.People, "Tenants", tint = PrimaryIndigo)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Active Tenants",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted)
                        )
                        Text(
                            text = activeTenantsCount.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = TextColorDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // stat card 2
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToMaintenance() },
                    colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WarningAmberBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Engineering, "Maintenance", tint = WarningAmber)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Open Tickets",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted)
                        )
                        Text(
                            text = openMaintenanceCount.toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = TextColorDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION: Quick Workflow Jumps
            Text(
                text = "OPERATIONAL HUB",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = TextColorMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PGFlowActionRow(
                title = "Manage Rent Payments",
                description = "Track, review, or collect outstanding lease sums.",
                icon = Icons.Default.ReceiptLong,
                colorAccent = ActionBlue,
                onClick = onNavigateToRent
            )

            Spacer(modifier = Modifier.height(10.dp))

            PGFlowActionRow(
                title = "Maintenance Ticketing",
                description = "Oversee open repairs or file emergency tickets.",
                icon = Icons.Default.Build,
                colorAccent = WarningAmber,
                onClick = onNavigateToMaintenance
            )

            Spacer(modifier = Modifier.height(10.dp))

            PGFlowActionRow(
                title = "Lease & Portfolio Status",
                description = "Register and manage properties and building units.",
                icon = Icons.Default.HomeWork,
                colorAccent = SuccessGreen,
                onClick = onNavigateToProperties
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PGFlowActionRow(
    title: String,
    description: String,
    icon: ImageVector,
    colorAccent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OutlineColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = colorAccent)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = TextColorDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextColorMuted,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextColorMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Helper wrapper to enforce standard visual margin padding
@Composable
fun PaddingScope(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        content = content
    )
}

// -------------------------------------------------------------
// TAB 2: PROPERTIES SCREEN
// -------------------------------------------------------------
@Composable
fun PropertiesScreen(
    properties: List<Property>,
    preferredCurrency: String = "USD",
    onAddProperty: (name: String, address: String, totalUnits: Int, rentAmount: Double, type: String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Properties",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = TextColorDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${properties.size} Active Buildings",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted)
                    )
                }

                Button(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                    modifier = Modifier.testTag("add_property_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Property")
                }
            }

            if (properties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.HomeWork,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextColorMuted.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No properties found",
                            style = MaterialTheme.typography.headlineSmall.copy(color = TextColorMuted)
                        )
                        Text(
                            "Register your first property to begin managing leases.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(properties, key = { it.id }) { property ->
                        PropertyCard(property = property, preferredCurrency = preferredCurrency)
                    }
                }
            }
        }

        if (showDialog) {
            AddPropertyDialog(
                preferredCurrency = preferredCurrency,
                onDismiss = { showDialog = false },
                onConfirm = { name, address, units, rentArr, type ->
                    onAddProperty(name, address, units, rentArr, type)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun PropertyCard(property: Property, preferredCurrency: String = "USD") {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("property_card_${property.id}"),
        colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OutlineColor)
    ) {
        Column {
            // Property banner image
            AsyncImage(
                model = property.imageUrl ?: "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=500&auto=format&fit=crop&q=60",
                contentDescription = property.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = property.name,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = TextColorDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TextColorMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = property.address,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextColorMuted,
                                    fontSize = 13.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    StatusPill(text = property.type, severity = "neutral")
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = OutlineColor)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Avg Monthly Rent",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextColorMuted,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = formatCurrency(property.rentAmount, preferredCurrency),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Occupancy Rate",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextColorMuted,
                                fontSize = 12.sp
                            )
                        )
                        val pct = if (property.totalUnits > 0) (property.occupiedUnits.toFloat() / property.totalUnits * 100).toInt() else 0
                        Text(
                            text = "$pct% (${property.occupiedUnits}/${property.totalUnits} Units)",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = if (pct > 75) SuccessGreen else if (pct > 40) WarningAmber else DangerRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddPropertyDialog(
    preferredCurrency: String = "USD",
    onDismiss: () -> Unit,
    onConfirm: (name: String, address: String, totalUnits: Int, rentAmount: Double, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var totalUnits by remember { mutableStateOf("") }
    var rentAmount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Apartment") }

    val propertyTypes = listOf("Apartment", "Townhouse", "Commercial", "Single Family")
    var isExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_property_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Property",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextColorDark,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Property Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prop_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ActionBlue)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prop_address_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ActionBlue)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = totalUnits,
                        onValueChange = { totalUnits = it },
                        label = { Text("Total Units") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("prop_units_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ActionBlue)
                    )

                    OutlinedTextField(
                        value = rentAmount,
                        onValueChange = { rentAmount = it },
                        label = { Text("Rent (${getCurrencySymbol(preferredCurrency)})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("prop_rent_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ActionBlue)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Property Type", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { isExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("prop_type_dropdown_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(type, color = TextColorDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TextColorMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                        modifier = Modifier.background(PureWhiteCard)
                    ) {
                        propertyTypes.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    type = item
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_dismiss_btn")) {
                        Text("Cancel", color = TextColorMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val uVal = totalUnits.toIntOrNull() ?: 1
                            val rVal = rentAmount.toDoubleOrNull() ?: 1000.0
                            if (name.isNotBlank()) {
                                onConfirm(name, address, uVal, rVal, type)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("dialog_confirm_btn")
                    ) {
                        Text("Save Property")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: TENANTS ONBOARDING SCREEN
// -------------------------------------------------------------
@Composable
fun TenantsScreen(
    tenants: List<Tenant>,
    properties: List<Property>,
    onAddTenant: (name: String, email: String, phone: String, propertyId: Int, unitNo: String, leaseS: String, leaseE: String, status: String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTenants = remember(tenants, searchQuery) {
        if (searchQuery.isBlank()) tenants
        else tenants.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.propertyName.contains(searchQuery, ignoreCase = true) ||
            it.unitNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tenants",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = TextColorDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Onboarding & Directory",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted)
                    )
                }

                Button(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                    modifier = Modifier.testTag("add_tenant_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Tenant")
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, building, unit...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextColorMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .testTag("tenant_search_bar"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PureWhiteCard,
                    unfocusedContainerColor = PureWhiteCard,
                    focusedBorderColor = ActionBlue,
                    unfocusedBorderColor = OutlineColor
                )
            )

            if (filteredTenants.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextColorMuted.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No tenants found",
                            style = MaterialTheme.typography.headlineSmall.copy(color = TextColorMuted)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredTenants, key = { it.id }) { tenant ->
                        TenantCard(tenant = tenant)
                    }
                }
            }
        }

        if (showDialog) {
            AddTenantDialog(
                properties = properties,
                onDismiss = { showDialog = false },
                onConfirm = { name, email, phone, propId, unitNo, start, end, status ->
                    onAddTenant(name, email, phone, propId, unitNo, start, end, status)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun TenantCard(tenant: Tenant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tenant_card_${tenant.id}"),
        colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, OutlineColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigoContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tenant.name.take(2).uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = tenant.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = TextColorDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "@${tenant.unitNumber}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ActionBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                StatusPill(text = tenant.status, severity = if (tenant.status == "Active") "success" else "warning")
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = OutlineColor)
            Spacer(modifier = Modifier.height(12.dp))

            // Lease particulars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Business, null, tint = TextColorMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tenant.propertyName,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 13.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, tint = TextColorMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${tenant.leaseStart} to ${tenant.leaseEnd}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contact shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, null, tint = TextColorMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tenant.email,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 12.sp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, null, tint = TextColorMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tenant.phone,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 12.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddTenantDialog(
    properties: List<Property>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String, propertyId: Int, unitNo: String, leaseS: String, leaseE: String, status: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var unitNumber by remember { mutableStateOf("") }
    var leaseStart by remember { mutableStateOf("2026-06-17") }
    var leaseEnd by remember { mutableStateOf("2027-06-17") }
    var status by remember { mutableStateOf("Active") }

    var selectedPropId by remember { mutableStateOf(properties.firstOrNull()?.id ?: 0) }
    val selectedPropName = remember(selectedPropId, properties) {
        properties.find { it.id == selectedPropId }?.name ?: "Select Property"
    }

    var propDropdownExpanded by remember { mutableStateOf(false) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_tenant_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Tenant",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextColorDark,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tenant Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tenant_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tenant_email_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tenant_phone_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Assign Property", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                // Real Property Picker Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { propDropdownExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("tenant_property_dropdown_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedPropName, color = TextColorDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TextColorMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = propDropdownExpanded,
                        onDismissRequest = { propDropdownExpanded = false },
                        modifier = Modifier.background(PureWhiteCard)
                    ) {
                        properties.forEach { prop ->
                            DropdownMenuItem(
                                text = { Text(prop.name) },
                                onClick = {
                                    selectedPropId = prop.id
                                    propDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = unitNumber,
                    onValueChange = { unitNumber = it },
                    label = { Text("Unit Number (e.g. Apt 402)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tenant_unit_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = leaseStart,
                        onValueChange = { leaseStart = it },
                        label = { Text("Lease Start") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tenant_lease_start_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = leaseEnd,
                        onValueChange = { leaseEnd = it },
                        label = { Text("Lease End") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tenant_lease_end_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Onboarding Status", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                // Onboarding status selection
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { statusDropdownExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("tenant_status_dropdown_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(status, color = TextColorDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TextColorMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = statusDropdownExpanded,
                        onDismissRequest = { statusDropdownExpanded = false },
                        modifier = Modifier.background(PureWhiteCard)
                    ) {
                        listOf("Active", "Pending Onboarding").forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    status = item
                                    statusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_dismiss_btn")) {
                        Text("Cancel", color = TextColorMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && selectedPropId != 0 && unitNumber.isNotBlank()) {
                                onConfirm(name, email, phone, selectedPropId, unitNumber, leaseStart, leaseEnd, status)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("dialog_confirm_btn")
                    ) {
                        Text("Save Tenant")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: OPERATIONS (RENT & MAINTENANCE CONSOLIDATION)
// -------------------------------------------------------------
@Composable
fun OperationsScreen(
    payments: List<RentPayment>,
    maintenanceRequests: List<MaintenanceRequest>,
    properties: List<Property>,
    tenants: List<Tenant>,
    preferredCurrency: String = "USD",
    onTogglePayment: (RentPayment) -> Unit,
    onAddPayment: (tenantId: Int, amount: Double, due: String, status: String) -> Unit,
    onAddRequest: (propertyId: Int, unitNo: String, title: String, desc: String, urgency: String) -> Unit,
    onUpdateRequestStatus: (requestId: Int, status: String) -> Unit
) {
    var operationTab by remember { mutableStateOf(0) } // 0 = Rent, 1 = Maintenance

    var showRentDialog by remember { mutableStateOf(false) }
    var showMaintenanceDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Selector Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ActionBlueContainer)
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (operationTab == 0) ActionBlue else Color.Transparent)
                        .clickable { operationTab = 0 }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("operations_rent_tab_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Rent Collection",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (operationTab == 0) PrimaryIndigoContainer else TextColorMuted,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (operationTab == 1) ActionBlue else Color.Transparent)
                        .clickable { operationTab = 1 }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("operations_maintenance_tab_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Maintenance Logs",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (operationTab == 1) PrimaryIndigoContainer else TextColorMuted,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Quick Add Operations button matching the operational tab
            Button(
                onClick = {
                    if (operationTab == 0) showRentDialog = true
                    else showMaintenanceDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("operations_quick_add_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (operationTab == 0) "Expected Rent" else "File Case", fontSize = 13.sp)
            }
        }

        // Active layout contents
        Box(modifier = Modifier.weight(1f)) {
            if (operationTab == 0) {
                RentCollectionList(
                    payments = payments,
                    preferredCurrency = preferredCurrency,
                    onTogglePayment = onTogglePayment
                )
            } else {
                MaintenanceRequestList(
                    requests = maintenanceRequests,
                    onUpdateStatus = onUpdateRequestStatus
                )
            }
        }

        // Forms dialogs
        if (showRentDialog) {
            AddExpectedRentDialog(
                tenants = tenants,
                onDismiss = { showRentDialog = false },
                onConfirm = { tenantId, amount, due, status ->
                    onAddPayment(tenantId, amount, due, status)
                    showRentDialog = false
                }
            )
        }

        if (showMaintenanceDialog) {
            AddMaintenanceDialog(
                properties = properties,
                onDismiss = { showMaintenanceDialog = false },
                onConfirm = { propId, unitNo, title, desc, urgency ->
                    onAddRequest(propId, unitNo, title, desc, urgency)
                    showMaintenanceDialog = false
                }
            )
        }
    }
}

// ----------------- OPERATIONS: RENT COLLECTION SUBVIEW -----------------
@Composable
fun RentCollectionList(
    payments: List<RentPayment>,
    preferredCurrency: String = "USD",
    onTogglePayment: (RentPayment) -> Unit
) {
    if (payments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No expected rent statements logged.", style = MaterialTheme.typography.bodyLarge.copy(color = TextColorMuted))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(payments, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rent_payment_card_${item.id}"),
                    colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = item.tenantName,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = TextColorDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = "${item.propertyName} • Due ${item.dueDate}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 12.sp)
                                )
                            }

                            StatusPill(text = item.status, severity = item.status)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = OutlineColor)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Rent Amount",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 12.sp)
                                )
                                Text(
                                    text = formatCurrency(item.amount, preferredCurrency),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = PrimaryIndigo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                            }

                            Button(
                                onClick = { onTogglePayment(item) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (item.status == "Paid") Color(0xFFEDEEEF) else ActionBlue
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("rent_payment_action_${item.id}")
                            ) {
                                Text(
                                    text = if (item.status == "Paid") "Mark Unpaid" else "Collect Rent",
                                    color = if (item.status == "Paid") TextColorDark else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpectedRentDialog(
    tenants: List<Tenant>,
    onDismiss: () -> Unit,
    onConfirm: (tenantId: Int, amount: Double, due: String, status: String) -> Unit
) {
    var selectTenantId by remember { mutableStateOf(tenants.firstOrNull()?.id ?: 0) }
    val selectTenantName = remember(selectTenantId, tenants) {
        tenants.find { it.id == selectTenantId }?.name ?: "Select Tenant"
    }

    var amount by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-07-01") }
    var status by remember { mutableStateOf("Pending") }

    var tDropdownExpanded by remember { mutableStateOf(false) }
    var sDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_rent_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Log Expected Rent",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextColorDark,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Tenant Billing Target", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { tDropdownExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("rent_tenant_dropdown_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectTenantName, color = TextColorDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TextColorMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = tDropdownExpanded,
                        onDismissRequest = { tDropdownExpanded = false },
                        modifier = Modifier.background(PureWhiteCard)
                    ) {
                        tenants.forEach { t ->
                            DropdownMenuItem(
                                text = { Text("${t.name} (${t.propertyName})") },
                                onClick = {
                                    selectTenantId = t.id
                                    tDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Rent Sum ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rent_amount_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rent_due_date_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Current Payments State", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { sDropdownExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("rent_state_dropdown_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(status, color = TextColorDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TextColorMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = sDropdownExpanded,
                        onDismissRequest = { sDropdownExpanded = false },
                        modifier = Modifier.background(PureWhiteCard)
                    ) {
                        listOf("Pending", "Paid", "Overdue").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    status = s
                                    sDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_dismiss_btn")) {
                        Text("Cancel", color = TextColorMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val aVal = amount.toDoubleOrNull() ?: 1200.0
                            if (selectTenantId != 0 && amount.isNotBlank()) {
                                onConfirm(selectTenantId, aVal, dueDate, status)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("dialog_confirm_btn")
                    ) {
                        Text("Log Statement")
                    }
                }
            }
        }
    }
}

// ----------------- OPERATIONS: MAINTENANCE LOGS SUBVIEW -----------------
@Composable
fun MaintenanceRequestList(
    requests: List<MaintenanceRequest>,
    onUpdateStatus: (requestId: Int, status: String) -> Unit
) {
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No maintenance requests registered.", style = MaterialTheme.typography.bodyLarge.copy(color = TextColorMuted))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(requests, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("maintenance_card_${item.id}"),
                    colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusPill(text = item.status, severity = if (item.status == "Completed") "success" else "warning")
                                Spacer(modifier = Modifier.width(10.dp))
                                StatusPill(text = item.urgency, severity = if (item.urgency == "High") "danger" else "neutral")
                            }

                            Text(
                                text = item.createdAt,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 12.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = TextColorDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextColorMuted, fontSize = 13.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = TextColorMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${item.propertyName} • ${item.unitNumber}",
                                style = MaterialTheme.typography.labelLarge.copy(color = TextColorMuted, fontSize = 12.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = OutlineColor)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Cycle through states
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.status != "Completed") {
                                OutlinedButton(
                                    onClick = {
                                        val next = if (item.status == "Open") "In Progress" else "Completed"
                                        onUpdateStatus(item.id, next)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, OutlineColor),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("maintenance_action_${item.id}")
                                ) {
                                    Text(
                                        text = if (item.status == "Open") "Start Action" else "Resolve Case",
                                        color = TextColorDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    "Resolved",
                                    style = MaterialTheme.typography.labelLarge.copy(color = SuccessGreen),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddMaintenanceDialog(
    properties: List<Property>,
    onDismiss: () -> Unit,
    onConfirm: (propertyId: Int, unitNo: String, title: String, desc: String, urgency: String) -> Unit
) {
    var selectPropId by remember { mutableStateOf(properties.firstOrNull()?.id ?: 0) }
    val selectPropName = remember(selectPropId, properties) {
        properties.find { it.id == selectPropId }?.name ?: "Select Property"
    }

    var unitNumber by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Medium") }

    var pDropdownExpanded by remember { mutableStateOf(false) }
    var uDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhiteCard),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_maintenance_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "File Maintenance Case",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextColorDark,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Target Property", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { pDropdownExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("maintenance_prop_dropdown_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectPropName, color = TextColorDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TextColorMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = pDropdownExpanded,
                        onDismissRequest = { pDropdownExpanded = false },
                        modifier = Modifier.background(PureWhiteCard)
                    ) {
                        properties.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    selectPropId = p.id
                                    pDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = unitNumber,
                    onValueChange = { unitNumber = it },
                    label = { Text("Unit/Suite ID (e.g. Apt 204)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("maintenance_unit_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Title (e.g. Water leak)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("maintenance_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("maintenance_desc_input"),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Urgency Level", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { uDropdownExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("maintenance_urgency_dropdown_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(urgency, color = TextColorDark)
                            Icon(Icons.Default.ArrowDropDown, null, tint = TextColorMuted)
                        }
                    }

                    DropdownMenu(
                        expanded = uDropdownExpanded,
                        onDismissRequest = { uDropdownExpanded = false },
                        modifier = Modifier.background(PureWhiteCard)
                    ) {
                        listOf("Low", "Medium", "High").forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    urgency = item
                                    uDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_dismiss_btn")) {
                        Text("Cancel", color = TextColorMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (selectPropId != 0 && unitNumber.isNotBlank() && title.isNotBlank()) {
                                onConfirm(selectPropId, unitNumber, title, description, urgency)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("dialog_confirm_btn")
                    ) {
                        Text("File Case")
                    }
                }
            }
        }
    }
}
