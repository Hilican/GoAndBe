@Composable
fun TripListScreen() {

    val mockTrips = listOf("Paris 2026", "Tokyo Adventure", "Swiss Alps")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Trips",
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 2. A simple list
        LazyColumn {
            items(mockTrips) { tripName ->
                Card(
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Mocked Image Placeholder
                        Box(modifier = Modifier.size(60.dp).background(MaterialTheme.colors.secondary))

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(text = tripName, style = MaterialTheme.typography.h6)
                            Text(text = "Tap to view itinerary", style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }
        }
    }
}