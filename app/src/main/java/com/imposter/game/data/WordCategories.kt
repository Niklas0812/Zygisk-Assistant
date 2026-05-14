package com.imposter.game.data

data class WordCategory(
    val id: String,
    val name: String,
    val emoji: String,
    val words: List<String>,
)

object WordCategories {

    val all: List<WordCategory> = listOf(
        WordCategory(
            id = "animals",
            name = "Animals",
            emoji = "🐾",
            words = listOf(
                "Elephant", "Tiger", "Penguin", "Dolphin", "Kangaroo", "Cheetah",
                "Octopus", "Giraffe", "Flamingo", "Panda", "Wolf", "Eagle",
                "Crocodile", "Hedgehog", "Squirrel", "Koala", "Chameleon",
                "Hippopotamus", "Rhinoceros", "Owl", "Bat", "Shark", "Whale",
                "Lion", "Bear", "Fox", "Deer", "Rabbit", "Hamster", "Parrot",
                "Turtle", "Frog", "Snake", "Spider", "Butterfly", "Bee",
                "Dragonfly", "Crab", "Lobster", "Seahorse",
            ),
        ),
        WordCategory(
            id = "food",
            name = "Food",
            emoji = "🍕",
            words = listOf(
                "Pizza", "Sushi", "Burger", "Pasta", "Tacos", "Ramen", "Curry",
                "Pancakes", "Waffles", "Salad", "Steak", "Dumplings", "Burrito",
                "Lasagna", "Croissant", "Bagel", "Donut", "Cupcake", "Ice Cream",
                "Cheesecake", "Tiramisu", "Brownie", "Cookies", "Chocolate",
                "Sandwich", "Hot Dog", "Nachos", "Falafel", "Kebab", "Paella",
                "Pho", "Pad Thai", "Risotto", "Gnocchi", "Fries", "Popcorn",
                "Pretzel", "Pancake", "Smoothie", "Sorbet",
            ),
        ),
        WordCategory(
            id = "places",
            name = "Places",
            emoji = "🌍",
            words = listOf(
                "Paris", "Tokyo", "New York", "London", "Rome", "Sydney",
                "Beach", "Mountain", "Desert", "Jungle", "Forest", "Island",
                "Castle", "Pyramid", "Volcano", "Glacier", "Waterfall", "Cave",
                "Lighthouse", "Stadium", "Airport", "Library", "Museum",
                "Hospital", "School", "Park", "Zoo", "Aquarium", "Cinema",
                "Bakery", "Pharmacy", "Restaurant", "Hotel", "Beach Resort",
                "Ski Resort", "Theme Park", "Concert Hall", "Bus Station",
                "Subway", "Harbor",
            ),
        ),
        WordCategory(
            id = "objects",
            name = "Objects",
            emoji = "💡",
            words = listOf(
                "Umbrella", "Telescope", "Backpack", "Camera", "Headphones",
                "Keyboard", "Mirror", "Candle", "Clock", "Pillow", "Glasses",
                "Wallet", "Suitcase", "Toothbrush", "Hairbrush", "Bicycle",
                "Skateboard", "Guitar", "Piano", "Drum", "Microphone",
                "Smartphone", "Laptop", "Tablet", "Television", "Refrigerator",
                "Microwave", "Toaster", "Blender", "Vacuum", "Hammer",
                "Screwdriver", "Drill", "Ladder", "Paintbrush", "Scissors",
                "Stapler", "Magnet", "Compass", "Flashlight",
            ),
        ),
        WordCategory(
            id = "sports",
            name = "Sports",
            emoji = "⚽",
            words = listOf(
                "Football", "Basketball", "Tennis", "Baseball", "Soccer",
                "Cricket", "Rugby", "Golf", "Hockey", "Volleyball", "Boxing",
                "Karate", "Judo", "Wrestling", "Swimming", "Diving", "Surfing",
                "Skiing", "Snowboarding", "Skateboarding", "Cycling", "Running",
                "Marathon", "Climbing", "Archery", "Fencing", "Bowling",
                "Badminton", "Table Tennis", "Handball", "Polo", "Lacrosse",
                "Rowing", "Sailing", "Kayaking", "Gymnastics", "Yoga",
                "Pilates", "Curling", "Bobsled",
            ),
        ),
        WordCategory(
            id = "movies",
            name = "Movies",
            emoji = "🎬",
            words = listOf(
                "Titanic", "Inception", "Avatar", "Frozen", "Joker", "Gladiator",
                "Jaws", "Shrek", "Aladdin", "Tangled", "Cinderella", "Coco",
                "Up", "Wall-E", "Ratatouille", "Toy Story", "Encanto", "Moana",
                "Mulan", "Pocahontas", "The Lion King", "Beauty and the Beast",
                "Snow White", "Bambi", "Dumbo", "Pinocchio", "Hercules",
                "Tarzan", "Brave", "Soul",
            ),
        ),
        WordCategory(
            id = "professions",
            name = "Professions",
            emoji = "👨‍⚖️",
            words = listOf(
                "Doctor", "Teacher", "Engineer", "Pilot", "Firefighter",
                "Police Officer", "Chef", "Baker", "Farmer", "Astronaut",
                "Scientist", "Architect", "Lawyer", "Judge", "Dentist", "Nurse",
                "Plumber", "Electrician", "Carpenter", "Mechanic", "Painter",
                "Photographer", "Journalist", "Writer", "Actor", "Musician",
                "Dancer", "Singer", "Director", "Producer", "Programmer",
                "Designer", "Veterinarian", "Pharmacist", "Surgeon", "Therapist",
                "Librarian", "Translator", "Postman", "Magician",
            ),
        ),
        WordCategory(
            id = "fruits",
            name = "Fruits",
            emoji = "🍎",
            words = listOf(
                "Apple", "Banana", "Orange", "Strawberry", "Pineapple", "Mango",
                "Watermelon", "Grape", "Cherry", "Peach", "Pear", "Plum",
                "Kiwi", "Lemon", "Lime", "Blueberry", "Raspberry", "Blackberry",
                "Coconut", "Avocado", "Pomegranate", "Papaya", "Guava",
                "Lychee", "Dragon Fruit", "Passion Fruit", "Apricot", "Fig",
                "Date", "Cranberry",
            ),
        ),
        WordCategory(
            id = "instruments",
            name = "Instruments",
            emoji = "🎸",
            words = listOf(
                "Guitar", "Piano", "Violin", "Drums", "Flute", "Saxophone",
                "Trumpet", "Clarinet", "Cello", "Harp", "Accordion", "Banjo",
                "Bass Guitar", "Trombone", "Oboe", "Tuba", "Harmonica",
                "Ukulele", "Mandolin", "Bagpipes", "Xylophone", "Synthesizer",
                "Bongos", "Tambourine", "Maracas", "Triangle", "Cymbals",
                "French Horn", "Recorder", "Sitar",
            ),
        ),
        WordCategory(
            id = "tech",
            name = "Tech",
            emoji = "💻",
            words = listOf(
                "Smartphone", "Laptop", "Tablet", "Smartwatch", "Headphones",
                "Drone", "Robot", "Camera", "Printer", "Scanner", "Router",
                "Monitor", "Keyboard", "Mouse", "Webcam", "Microphone",
                "Speaker", "Console", "VR Headset", "Power Bank", "USB Drive",
                "Hard Drive", "Server", "Satellite", "GPS", "Bluetooth",
                "WiFi", "Cloud", "Algorithm", "Database",
            ),
        ),
    )

    fun byId(id: String): WordCategory? = all.firstOrNull { it.id == id }
}
