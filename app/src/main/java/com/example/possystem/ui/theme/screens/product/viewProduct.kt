

package com.example.possystem.ui.theme.screens.product



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.possystem.data.ProductViewModel
import com.example.possystem.models.ProductModel

@Composable
fun ProductListScreen(navController: NavController) {
    // lowercase variable name is standard Kotlin practice
    val productViewModel: ProductViewModel = viewModel()
    val products = productViewModel.products
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        productViewModel.fetchProduct(context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentPadding = PaddingValues(vertical = 50.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 'key' helps the list update correctly when an item is deleted
        items(products, key = { it.id ?: it.hashCode() }) { product ->
            ProductCard(
                product = product,
                onDelete = { productId ->
                    // Logic to trigger the actual deletion in your database

                },
                navController = navController
            )
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onDelete: (String) -> Unit,
    navController: NavController
) {
    var showDialog by remember { mutableStateOf(false) }

    // Delete Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete ${product.product_name}?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    // Only calls onDelete if the ID exists
                    product.id?.let { onDelete(it) }
                }) {
                    Text("Yes", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Product Image
                product.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Product Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.product_name ?: "Unknown Product",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Price: KSh ${product.price}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { navController.navigate("update_product/${product.id}") }
                ) {
                    Text("Update")
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = { showDialog = true }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}