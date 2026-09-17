#!/usr/bin/env python3
import json
import os

os.makedirs("app/src/main/assets", exist_ok=True)
os.makedirs("flutter/assets", exist_ok=True)

kids_questions = []
adults_questions = []

def add_q(q_list, qid, text, options, correct_idx, category, difficulty, explanation):
    q_list.append({
        "id": qid,
        "text": text,
        "options": options,
        "correctIndex": correct_idx,
        "correctAnswer": options[correct_idx],
        "category": category,
        "difficulty": difficulty,
        "explanation": explanation
    })

# ==========================================
# KIDS QUESTIONS (180 Total: 60 Easy, 60 Med, 60 Hard)
# ==========================================

# --- KIDS EASY (60) ---
kids_easy_data = [
    # Math
    ("What is 5 + 3?", ["6", "7", "8", "9"], 2, "Basic Math", "5 + 3 = 8."),
    ("What is 10 - 4?", ["5", "6", "7", "8"], 1, "Basic Math", "10 - 4 = 6."),
    ("What number comes after 19?", ["18", "20", "21", "22"], 1, "Basic Math", "20 comes directly after 19."),
    ("What is 2 x 4?", ["6", "7", "8", "9"], 2, "Basic Math", "2 times 4 equals 8."),
    ("Which number is the smallest?", ["12", "5", "8", "19"], 1, "Basic Math", "5 is smaller than 8, 12, and 19."),
    ("What is half of 10?", ["3", "4", "5", "6"], 2, "Basic Math", "Half of 10 is 5 because 5 + 5 = 10."),
    ("What is 7 + 0?", ["0", "7", "70", "1"], 1, "Basic Math", "Adding 0 to any number leaves it unchanged."),
    ("How many legs do 2 cats have in total?", ["4", "6", "8", "10"], 2, "Basic Math", "Each cat has 4 legs, so 2 cats have 4 + 4 = 8 legs."),
    # Shapes & Colors
    ("How many sides does a triangle have?", ["2", "3", "4", "5"], 1, "Shapes & Colors", "A triangle always has 3 sides."),
    ("What color do you get by mixing Red and Yellow?", ["Orange", "Green", "Purple", "Blue"], 0, "Shapes & Colors", "Red and yellow mix together to make orange."),
    ("What shape is a standard soccer ball?", ["Cube", "Sphere", "Cylinder", "Pyramid"], 1, "Shapes & Colors", "A ball is shaped like a sphere (3D circle)."),
    ("How many corners does a square have?", ["3", "4", "5", "6"], 1, "Shapes & Colors", "A square has 4 equal corners and 4 equal sides."),
    ("What color is an emerald?", ["Red", "Blue", "Green", "Yellow"], 2, "Shapes & Colors", "Emeralds are famous precious stones that are green."),
    ("How many colors are in a rainbow?", ["5", "6", "7", "8"], 2, "Shapes & Colors", "A rainbow has 7 colors: VIBGYOR."),
    ("What shape is a book or a door typically?", ["Circle", "Triangle", "Rectangle", "Hexagon"], 2, "Shapes & Colors", "Books and standard doors have a rectangular shape."),
    # Animals & Birds
    ("Which animal is known as the King of the Jungle?", ["Tiger", "Lion", "Elephant", "Cheetah"], 1, "Animals & Birds", "The lion is traditionally called the King of the Jungle."),
    ("What is a baby dog called?", ["Kitten", "Puppy", "Cub", "Calf"], 1, "Animals & Birds", "A baby dog is called a puppy."),
    ("Which bird cannot fly but is an excellent swimmer?", ["Parrot", "Eagle", "Penguin", "Sparrow"], 2, "Animals & Birds", "Penguins cannot fly in the air, but they swim gracefully."),
    ("How many legs does a spider have?", ["6", "8", "10", "12"], 1, "Animals & Birds", "Spiders are arachnids and have 8 legs."),
    ("Which is the largest land animal in the world?", ["Hippopotamus", "African Elephant", "Rhinoceros", "Giraffe"], 1, "Animals & Birds", "The African bush elephant is the largest living land animal."),
    ("What do bees make?", ["Milk", "Honey", "Cotton", "Silk"], 1, "Animals & Birds", "Bees collect flower nectar to produce sweet honey."),
    ("What is a baby cat called?", ["Puppy", "Kitten", "Foal", "Joey"], 1, "Animals & Birds", "A baby cat is called a kitten."),
    ("Which animal has black and white stripes?", ["Zebra", "Leopard", "Panda", "Cheetah"], 0, "Animals & Birds", "Zebras are famous for their black and white striped coats."),
    # Countries & Capitals
    ("What is the capital of India?", ["Mumbai", "New Delhi", "Kolkata", "Bengaluru"], 1, "Countries & Capitals", "New Delhi is the official capital city of India."),
    ("Which country is famous for the Eiffel Tower?", ["Germany", "Italy", "France", "Spain"], 2, "Countries & Capitals", "The Eiffel Tower is located in Paris, France."),
    ("What is the capital of the United Kingdom?", ["Paris", "London", "Dublin", "Rome"], 1, "Countries & Capitals", "London is the capital of the United Kingdom."),
    ("Which country has the Pyramids of Giza?", ["Egypt", "Brazil", "India", "Greece"], 0, "Countries & Capitals", "The Great Pyramids are located in Giza, Egypt."),
    ("What is the national animal of India?", ["Lion", "Elephant", "Royal Bengal Tiger", "Peacock"], 2, "Countries & Capitals", "The Royal Bengal Tiger is India's national animal."),
    ("What is the national bird of India?", ["Parrot", "Indian Peacock", "Sparrow", "Pigeon"], 1, "Countries & Capitals", "The Indian Peacock (Pavo cristatus) is the national bird."),
    # Indian States & Capitals
    ("What is the capital of Maharashtra?", ["Pune", "Nagpur", "Mumbai", "Nashik"], 2, "Indian States", "Mumbai is the financial capital and state capital of Maharashtra."),
    ("What is the capital of Tamil Nadu?", ["Madurai", "Chennai", "Coimbatore", "Salem"], 1, "Indian States", "Chennai (formerly Madras) is the capital of Tamil Nadu."),
    ("What is the capital of Karnataka?", ["Mysuru", "Bengaluru", "Hubli", "Mangaluru"], 1, "Indian States", "Bengaluru (Bangalore) is the capital of Karnataka."),
    ("What is the capital of West Bengal?", ["Kolkata", "Siliguri", "Darjeeling", "Asansol"], 0, "Indian States", "Kolkata is the capital city of West Bengal."),
    ("What is the capital of Rajasthan?", ["Udaipur", "Jodhpur", "Jaipur", "Bikaner"], 2, "Indian States", "Jaipur, known as the Pink City, is the capital of Rajasthan."),
    # Simple Science
    ("Which planet do we live on?", ["Mars", "Venus", "Earth", "Jupiter"], 2, "Simple Science", "We live on planet Earth, the third planet from the Sun."),
    ("What shines brightly in the sky during the day?", ["Moon", "Sun", "Mars", "North Star"], 1, "Simple Science", "The Sun is the star that lights up our daytime."),
    ("What do humans breathe in to stay alive?", ["Carbon dioxide", "Oxygen", "Helium", "Nitrogen only"], 1, "Simple Science", "Humans need oxygen from the air to breathe and live."),
    ("What happens to water when it freezes?", ["Turns to steam", "Turns into ice", "Disappears", "Catches fire"], 1, "Simple Science", "When water freezes below 0°C, it becomes solid ice."),
    ("How many days are there in a standard year?", ["350", "365", "370", "360"], 1, "Simple Science", "A standard calendar year has 365 days (leap year has 366)."),
    ("Which part of a plant grows under the ground?", ["Flower", "Leaf", "Root", "Stem"], 2, "Simple Science", "Roots grow underground to absorb water and minerals."),
    ("What is the boiling point of pure water?", ["50°C", "100°C", "150°C", "200°C"], 1, "Simple Science", "Water boils at 100 degrees Celsius at sea level."),
    ("How many teeth does an adult human typically have?", ["20", "28", "32", "36"], 2, "Simple Science", "A full set of adult human permanent teeth contains 32 teeth."),
    # Landmarks & World GK
    ("Where is the Taj Mahal located?", ["Delhi", "Agra", "Jaipur", "Varanasi"], 1, "Landmarks", "The Taj Mahal is located in Agra on the banks of the Yamuna River."),
    ("Which is the tallest animal in the world?", ["Elephant", "Giraffe", "Camel", "Ostrich"], 1, "World GK", "Giraffes are the tallest living mammals in the world."),
    ("How many continents are there on Earth?", ["5", "6", "7", "8"], 2, "World GK", "Earth has 7 continents: Asia, Africa, North America, South America, Antarctica, Europe, Australia."),
    ("Which is the largest ocean on Earth?", ["Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"], 2, "World GK", "The Pacific Ocean is the largest and deepest ocean on Earth."),
    ("What is the color of an apple usually?", ["Red", "Purple", "Blue", "Black"], 0, "Shapes & Colors", "Apples are most commonly red or green."),
    ("How many hours are there in one whole day?", ["12", "20", "24", "48"], 2, "Simple Science", "One complete day and night cycle is 24 hours."),
    ("Which sense organ do we use to smell things?", ["Eyes", "Ears", "Nose", "Tongue"], 2, "Simple Science", "We use our nose to detect scents and smells."),
    ("What do caterpillars turn into?", ["Bees", "Butterflies", "Frogs", "Spiders"], 1, "Animals & Birds", "A caterpillar undergoes metamorphosis to become a butterfly."),
    ("What is 6 + 6?", ["10", "11", "12", "14"], 2, "Basic Math", "6 + 6 equals 12."),
    ("What is 15 - 5?", ["8", "10", "12", "15"], 1, "Basic Math", "15 minus 5 is 10."),
    ("How many fingers are on two human hands?", ["8", "10", "12", "14"], 1, "Basic Math", "Each hand has 5 fingers, so two hands have 10 fingers."),
    ("What is the capital of Goa?", ["Margao", "Panaji", "Vasco da Gama", "Mapusa"], 1, "Indian States", "Panaji is the capital city of Goa."),
    ("What is the capital of Gujarat?", ["Ahmedabad", "Surat", "Gandhinagar", "Vadodara"], 2, "Indian States", "Gandhinagar is the capital city of Gujarat."),
    ("Which fruit is known as the King of Fruits in India?", ["Apple", "Mango", "Banana", "Orange"], 1, "World GK", "Mango is lovingly known as the King of Fruits in India."),
    ("What is the main color of the Sun?", ["Green", "Yellow", "Blue", "Violet"], 1, "Shapes & Colors", "The Sun appears yellow-white from Earth's surface."),
    ("Which animal gives us wool for warm clothes?", ["Cow", "Sheep", "Horse", "Dog"], 1, "Animals & Birds", "Sheep provide wool which is spun into warm clothes."),
    ("How many days are in a week?", ["5", "6", "7", "8"], 2, "Basic Math", "There are 7 days from Monday through Sunday."),
    ("What is the closest star to Earth?", ["Alpha Centauri", "Polaris", "The Sun", "Sirius"], 2, "Simple Science", "The Sun is our closest star, located 150 million km away.")
]

for i, (q, opts, cidx, cat, exp) in enumerate(kids_easy_data, 1):
    add_q(kids_questions, f"k_easy_{i:03d}", q, opts, cidx, cat, "Easy", exp)

# --- KIDS MEDIUM (60) ---
kids_med_data = [
    # Math
    ("What is 12 x 3?", ["32", "36", "38", "40"], 1, "Basic Math", "12 x 3 = 36."),
    ("What is 45 divided by 5?", ["7", "8", "9", "10"], 2, "Basic Math", "45 / 5 = 9."),
    ("What is the next number in pattern: 3, 6, 9, 12, __?", ["14", "15", "16", "18"], 1, "Basic Math", "The pattern adds 3 each time: 12 + 3 = 15."),
    ("How many minutes are in 2 hours?", ["60", "100", "120", "150"], 2, "Basic Math", "Each hour has 60 minutes, so 2 hours have 120 minutes."),
    ("What is 100 - 35?", ["55", "65", "70", "75"], 1, "Basic Math", "100 - 35 = 65."),
    ("How many tens are there in the number 80?", ["4", "6", "8", "10"], 2, "Basic Math", "80 contains exactly 8 tens."),
    ("What is 7 x 8?", ["54", "56", "58", "64"], 1, "Basic Math", "7 multiplied by 8 is 56."),
    ("If you have 3 quarters, how many cents do you have?", ["50", "75", "100", "25"], 1, "Basic Math", "Each quarter is 25 cents, so 3 quarters equal 75 cents."),
    # Shapes & Geometry
    ("How many sides does a pentagon have?", ["4", "5", "6", "7"], 1, "Shapes & Colors", "A pentagon is a polygon with 5 sides."),
    ("How many sides does a hexagon have?", ["5", "6", "7", "8"], 1, "Shapes & Colors", "A hexagon has 6 straight sides."),
    ("What is the perimeter of a square with side length 4 cm?", ["12 cm", "14 cm", "16 cm", "20 cm"], 2, "Shapes & Colors", "Perimeter of a square = 4 x side = 4 x 4 = 16 cm."),
    ("How many faces does a cube have?", ["4", "6", "8", "12"], 1, "Shapes & Colors", "A standard cube has 6 square faces."),
    ("Which 3D shape looks like an Egyptian pyramid?", ["Cone", "Square Pyramid", "Prism", "Cylinder"], 1, "Shapes & Colors", "The Egyptian pyramids are square-based pyramids."),
    # Animals & Habitat
    ("What is a group of lions called?", ["Pack", "Herd", "Pride", "Flock"], 2, "Animals & Birds", "A family group of lions is called a pride."),
    ("Which mammal is capable of true sustained flight?", ["Flying squirrel", "Bat", "Sugar glider", "Flying fish"], 1, "Animals & Birds", "Bats are the only mammals capable of true flight."),
    ("What is a baby kangaroo called?", ["Joey", "Cub", "Fawn", "Pup"], 0, "Animals & Birds", "A baby kangaroo is called a joey and stays in mom's pouch."),
    ("Which bird lays the largest eggs in the world?", ["Eagle", "Albatross", "Ostrich", "Emu"], 2, "Animals & Birds", "The ostrich lays the largest eggs of any living bird."),
    ("Which sea creature has three hearts and blue blood?", ["Shark", "Whale", "Octopus", "Dolphin"], 2, "Animals & Birds", "An octopus has three hearts and copper-based blue blood."),
    ("Which animal is known as the Ship of the Desert?", ["Horse", "Camel", "Donkey", "Yak"], 1, "Animals & Birds", "Camels are called Ships of the Desert for navigating dunes."),
    ("What type of animal is a frog?", ["Reptile", "Amphibian", "Mammal", "Insect"], 1, "Animals & Birds", "Frogs live both in water and on land, making them amphibians."),
    # Countries & World Capitals
    ("What is the capital of Japan?", ["Seoul", "Beijing", "Tokyo", "Bangkok"], 2, "Countries & Capitals", "Tokyo is the bustling capital city of Japan."),
    ("What is the capital of Australia?", ["Sydney", "Melbourne", "Canberra", "Brisbane"], 2, "Countries & Capitals", "Canberra is the purpose-built capital city of Australia."),
    ("What is the capital of Canada?", ["Toronto", "Vancouver", "Ottawa", "Montreal"], 2, "Countries & Capitals", "Ottawa is the national capital of Canada."),
    ("Which country has the flag with a red maple leaf?", ["Canada", "USA", "Mexico", "Switzerland"], 0, "Countries & Capitals", "The Canadian flag features a distinctive red maple leaf."),
    ("Which country is shaped like a boot on world maps?", ["Greece", "Italy", "Portugal", "Norway"], 1, "Countries & Capitals", "Italy's peninsula has a famous boot-like outline."),
    # Indian States & Capitals
    ("What is the capital of Kerala?", ["Kochi", "Thiruvananthapuram", "Kozhikode", "Thrissur"], 1, "Indian States", "Thiruvananthapuram is the capital city of Kerala."),
    ("What is the capital of Punjab and Haryana both?", ["Amritsar", "Chandigarh", "Ludhiana", "Gurugram"], 1, "Indian States", "Chandigarh serves as the joint capital of both Punjab and Haryana."),
    ("What is the capital of Odisha?", ["Cuttack", "Bhubaneswar", "Puri", "Rourkela"], 1, "Indian States", "Bhubaneswar, the Temple City, is the capital of Odisha."),
    ("What is the capital of Assam?", ["Guwahati", "Dispur", "Silchar", "Tezpur"], 1, "Indian States", "Dispur is the official capital city of Assam."),
    ("What is the capital of Telangana?", ["Hyderabad", "Warangal", "Nizamabad", "Karimnagar"], 0, "Indian States", "Hyderabad is the capital city of Telangana."),
    ("What is the capital of Madhya Pradesh?", ["Indore", "Bhopal", "Gwalior", "Jabalpur"], 1, "Indian States", "Bhopal, the City of Lakes, is the capital of Madhya Pradesh."),
    ("What is the capital of Bihar?", ["Gaya", "Patna", "Muzaffarpur", "Bhagalpur"], 1, "Indian States", "Patna, situated along the Ganges, is the capital of Bihar."),
    # Science & Space
    ("Which planet is known as the Red Planet?", ["Venus", "Mars", "Jupiter", "Saturn"], 1, "Simple Science", "Mars looks reddish due to abundant iron oxide on its surface."),
    ("What gas do green plants absorb from the air during photosynthesis?", ["Oxygen", "Carbon dioxide", "Helium", "Nitrogen"], 1, "Simple Science", "Plants take in carbon dioxide and release oxygen."),
    ("What is the hardest natural substance on Earth?", ["Gold", "Iron", "Diamond", "Quartz"], 2, "Simple Science", "Diamond is the hardest naturally occurring mineral."),
    ("Which planet has prominent visible rings around it?", ["Uranus", "Neptune", "Saturn", "Jupiter"], 2, "Simple Science", "Saturn is famous for its bright, wide system of icy rings."),
    ("How many bones are in the adult human body?", ["186", "206", "226", "256"], 1, "Simple Science", "An adult human skeleton consists of 206 bones."),
    ("What is the center of an atom called?", ["Electron", "Proton", "Nucleus", "Neutron"], 2, "Simple Science", "The dense center of an atom is called the nucleus."),
    ("What protects Earth from harmful solar radiation?", ["Ozone Layer", "Clouds", "Ocean water", "Mountains"], 0, "Simple Science", "The ozone layer in the stratosphere filters ultraviolet rays."),
    # World GK & Landmarks
    ("Which is the longest river in the world?", ["Amazon", "Nile", "Yangtze", "Mississippi"], 1, "World GK", "The Nile River in Africa is traditionally recognized as the longest."),
    ("Which is the highest mountain peak in the world?", ["K2", "Mount Everest", "Kangchenjunga", "Lhotse"], 1, "World GK", "Mount Everest in the Himalayas stands at 8,848.86 meters."),
    ("Which continent is known as the 'Dark Continent' historically?", ["Asia", "Africa", "South America", "Europe"], 1, "World GK", "Africa was historically referred to by Europeans as the Dark Continent."),
    ("In which country would you find the Colosseum?", ["Greece", "Italy", "Turkey", "Egypt"], 1, "Landmarks", "The Colosseum is an ancient amphitheater located in Rome, Italy."),
    ("Where is the Great Barrier Reef located?", ["South Africa", "Australia", "Brazil", "Indonesia"], 1, "Landmarks", "The Great Barrier Reef is off the northeast coast of Australia."),
    ("Which Indian city is known as the 'Pink City'?", ["Udaipur", "Jaipur", "Jodhpur", "Bhopal"], 1, "Indian States", "Jaipur was painted terracotta pink in 1876 to welcome Prince Albert."),
    ("Which Indian state has the longest coastline?", ["Maharashtra", "Gujarat", "Tamil Nadu", "Andhra Pradesh"], 1, "Indian States", "Gujarat has the longest mainland coastline in India (~1,600 km)."),
    ("Who was the first person to walk on the Moon?", ["Buzz Aldrin", "Neil Armstrong", "Yuri Gagarin", "Michael Collins"], 1, "World GK", "Neil Armstrong stepped onto the Moon during Apollo 11 in 1969."),
    ("What is the currency of the United States?", ["Pound", "Euro", "Dollar", "Yen"], 2, "Countries & Capitals", "The United States uses the US Dollar ($)."),
    ("What is the currency of Japan?", ["Yuan", "Won", "Yen", "Rupee"], 2, "Countries & Capitals", "The national currency of Japan is the Japanese Yen (¥)."),
    ("What is the national flower of India?", ["Rose", "Lotus", "Marigold", "Jasmine"], 1, "World GK", "The sacred Lotus (Nelumbo nucifera) is India's national flower."),
    ("What instrument is used to measure temperature?", ["Barometer", "Thermometer", "Speedometer", "Altimeter"], 1, "Simple Science", "A thermometer measures temperature in Celsius, Fahrenheit, or Kelvin."),
    ("What do we call animals that eat only plants?", ["Carnivores", "Herbivores", "Omnivores", "Insectivores"], 1, "Animals & Birds", "Herbivores feed exclusively on plants and vegetation."),
    ("What do we call animals that eat both plants and meat?", ["Carnivores", "Herbivores", "Omnivores", "Decomposers"], 2, "Animals & Birds", "Omnivores eat a balanced diet of plant and animal matter."),
    ("What is the freezing point of water?", ["0°C", "10°C", "32°C", "-5°C"], 0, "Simple Science", "Water freezes into ice at 0 degrees Celsius (32°F)."),
    ("Which continent has the fewest permanent human residents?", ["Australia", "Antarctica", "Europe", "South America"], 1, "World GK", "Antarctica has no native human population, only scientists."),
    ("What is the capital of Himachal Pradesh?", ["Dharamshala", "Shimla", "Manali", "Kullu"], 1, "Indian States", "Shimla is the principal capital of Himachal Pradesh."),
    ("What is 9 x 9?", ["72", "81", "88", "90"], 1, "Basic Math", "9 multiplied by 9 is 81."),
    ("What is 75 + 25?", ["90", "95", "100", "110"], 2, "Basic Math", "75 + 25 equals 100."),
    ("How many sides does an octagon have?", ["6", "7", "8", "9"], 2, "Shapes & Colors", "An octagon is a polygon with 8 sides (like a stop sign).")
]

for i, (q, opts, cidx, cat, exp) in enumerate(kids_med_data, 1):
    add_q(kids_questions, f"k_med_{i:03d}", q, opts, cidx, cat, "Medium", exp)

# --- KIDS HARD (60) ---
kids_hard_data = [
    # Math & Logic
    ("What is 15 x 6?", ["75", "80", "90", "95"], 2, "Basic Math", "15 x 6 = 90."),
    ("What is the square root of 64?", ["6", "7", "8", "9"], 2, "Basic Math", "8 x 8 = 64, so the square root is 8."),
    ("What is the perimeter of a rectangle with length 7 cm and width 3 cm?", ["10 cm", "14 cm", "20 cm", "21 cm"], 2, "Basic Math", "Perimeter = 2 x (7 + 3) = 2 x 10 = 20 cm."),
    ("What is the area of a square with side 6 cm?", ["24 cm²", "30 cm²", "36 cm²", "42 cm²"], 2, "Basic Math", "Area of a square = side x side = 6 x 6 = 36 cm²."),
    ("What is 144 divided by 12?", ["10", "11", "12", "14"], 2, "Basic Math", "144 / 12 = 12."),
    ("Which fraction is equivalent to 1/2?", ["2/3", "3/6", "4/6", "5/8"], 1, "Basic Math", "3/6 simplifies to 1/2 by dividing top and bottom by 3."),
    ("If a train leaves at 2:15 PM and arrives at 4:45 PM, how long was the trip?", ["2 hours", "2 hours 15 mins", "2 hours 30 mins", "2 hours 45 mins"], 2, "Basic Math", "From 2:15 to 4:45 is 2 hours and 30 minutes."),
    ("What is the Roman numeral for 50?", ["X", "L", "C", "D"], 1, "Basic Math", "L represents 50 in Roman numerals."),
    # Science & Nature
    ("What part of the plant cell carries out photosynthesis?", ["Mitochondria", "Chloroplast", "Nucleus", "Ribosome"], 1, "Simple Science", "Chloroplasts contain chlorophyll where photosynthesis takes place."),
    ("Which blood cells help fight infections and diseases in humans?", ["Red blood cells", "White blood cells", "Platelets", "Plasma"], 1, "Simple Science", "White blood cells are the body's primary immune defenders."),
    ("What is the largest organ of the human body?", ["Liver", "Brain", "Skin", "Lungs"], 2, "Simple Science", "The skin is the body's largest organ by mass and surface area."),
    ("What is the name of our home galaxy?", ["Andromeda", "Milky Way", "Whirlpool", "Sombrero"], 1, "Simple Science", "Our solar system resides inside the spiral Milky Way galaxy."),
    ("What makes plant leaves look green?", ["Carotene", "Chlorophyll", "Hemoglobin", "Melanin"], 1, "Simple Science", "Chlorophyll pigment absorbs blue and red light, reflecting green."),
    ("Which layer of Earth is composed of molten rock under the crust?", ["Inner Core", "Mantle", "Outer Core", "Atmosphere"], 1, "Simple Science", "The mantle lies directly beneath Earth's crust."),
    ("Sound travels fastest through which medium?", ["Air", "Water", "Solids (Steel)", "Vacuum"], 2, "Simple Science", "Molecules in solids are packed closely, transmitting sound fastest."),
    ("What causes ocean tides on Earth?", ["Wind currents", "Gravitational pull of Moon and Sun", "Earthquakes", "Underwater volcanoes"], 1, "Simple Science", "Tides are driven by the gravitational interaction with the Moon and Sun."),
    # Animals & Biology
    ("Which animal has the longest lifespan of any terrestrial animal?", ["Elephant", "Giant Tortoise", "Blue Whale", "Crocodile"], 1, "Animals & Birds", "Aldabra and Galapagos giant tortoises can live well past 150 years."),
    ("Which bird can fly backwards?", ["Swallow", "Hummingbird", "Swift", "Kingfisher"], 1, "Animals & Birds", "Hummingbirds have flexible shoulder joints allowing backward flight."),
    ("Which sea creature is known to be the fastest swimmer?", ["Sailfish", "Tuna", "Great White Shark", "Barracuda"], 0, "Animals & Birds", "The sailfish can burst-swim at speeds up to 110 km/h (68 mph)."),
    ("What is the only continent where spiders and reptiles are completely absent?", ["Australia", "Antarctica", "Arctic", "Greenland"], 1, "Animals & Birds", "Antarctica's extreme subzero conditions support no reptiles or spiders."),
    # Indian History & Geography
    ("Which river is considered the most sacred river in India?", ["Yamuna", "Ganga (Ganges)", "Godavari", "Brahmaputra"], 1, "Indian States", "The Ganga is celebrated as India's most holy and iconic river."),
    ("What is the capital of Uttarakhand?", ["Nainital", "Haridwar", "Dehradun", "Rishikesh"], 2, "Indian States", "Dehradun is the winter capital and largest city of Uttarakhand."),
    ("What is the capital of Sikkim?", ["Gangtok", "Namchi", "Pelling", "Gyalshing"], 0, "Indian States", "Gangtok is the scenic capital city of Sikkim."),
    ("What is the capital of Arunachal Pradesh?", ["Itanagar", "Tawang", "Pasighat", "Ziro"], 0, "Indian States", "Itanagar is the capital city of Arunachal Pradesh."),
    ("Which state in India is known as the 'Land of Five Rivers'?", ["Haryana", "Punjab", "Uttar Pradesh", "Gujarat"], 1, "Indian States", "Punjab gets its name from 'Punj' (five) and 'Aab' (waters/rivers)."),
    ("Which monument was built by Shah Jahan in memory of Mumtaz Mahal?", ["Red Fort", "Taj Mahal", "Qutub Minar", "Hawa Mahal"], 1, "Landmarks", "The Taj Mahal was commissioned in 1632 by Mughal Emperor Shah Jahan."),
    ("In which state is the ancient Nalanda University situated?", ["Uttar Pradesh", "Bihar", "Madhya Pradesh", "Odisha"], 1, "Indian States", "Nalanda Mahavihara was an ancient center of learning in Bihar."),
    # World Geography & Landmarks
    ("What is the smallest country in the world by area?", ["Monaco", "Vatican City", "Nauru", "San Marino"], 1, "World GK", "Vatican City covers just 0.49 square kilometers."),
    ("What is the capital of Australia?", ["Sydney", "Melbourne", "Canberra", "Perth"], 2, "Countries & Capitals", "Canberra is Australia's capital, chosen as a compromise between Sydney and Melbourne."),
    ("Which African country was formerly known as Abyssinia?", ["Kenya", "Ethiopia", "Sudan", "Somalia"], 1, "World GK", "Ethiopia was historically referred to internationally as Abyssinia."),
    ("Which desert is the largest hot desert in the world?", ["Gobi Desert", "Kalahari Desert", "Sahara Desert", "Thar Desert"], 2, "World GK", "The Sahara Desert in North Africa covers over 9 million sq km."),
    ("Which country has the most natural lakes in the world?", ["United States", "Russia", "Canada", "Finland"], 2, "World GK", "Canada contains over 60% of all the natural lakes on Earth."),
    ("What is the capital of New Zealand?", ["Auckland", "Wellington", "Christchurch", "Queenstown"], 1, "Countries & Capitals", "Wellington is the southern-most national capital in the world."),
    ("Which strait connects the Atlantic Ocean to the Mediterranean Sea?", ["Bering Strait", "Strait of Malacca", "Strait of Gibraltar", "Bosporus Strait"], 2, "World GK", "The Strait of Gibraltar connects the Atlantic to the Mediterranean."),
    ("In which country is Machu Picchu located?", ["Chile", "Peru", "Colombia", "Bolivia"], 1, "Landmarks", "Machu Picchu is a 15th-century Inca citadel set high in Peru's Andes."),
    ("What is the deepest known location in Earth's oceans?", ["Puerto Rico Trench", "Java Trench", "Mariana Trench", "Tonga Trench"], 2, "World GK", "Challenger Deep in the Mariana Trench plunges nearly 11,000 meters."),
    # Science & Astronomy
    ("Which planet rotates on its side with an axial tilt of 98 degrees?", ["Saturn", "Uranus", "Neptune", "Jupiter"], 1, "Simple Science", "Uranus has an extreme tilt of 98°, rotating almost horizontally."),
    ("What is the speed of light in vacuum approximately?", ["30,000 km/s", "150,000 km/s", "300,000 km/s", "3,000,000 km/s"], 2, "Simple Science", "Light travels at roughly 299,792 kilometers per second in a vacuum."),
    ("What is dry ice made of?", ["Solid Water", "Solid Carbon Dioxide", "Solid Nitrogen", "Solid Methane"], 1, "Simple Science", "Dry ice is solid carbon dioxide frozen at -78.5°C."),
    ("What is the chemical symbol for Gold?", ["Ag", "Au", "Fe", "Cu"], 1, "Simple Science", "Au comes from the Latin word 'Aurum', meaning shining dawn."),
    ("What is the chemical symbol for Iron?", ["Ir", "Fe", "In", "Pb"], 1, "Simple Science", "Fe comes from the Latin word 'Ferrum'."),
    ("Which atmospheric gas makes up roughly 78% of Earth's air?", ["Oxygen", "Carbon Dioxide", "Nitrogen", "Argon"], 2, "Simple Science", "Earth's atmosphere is roughly 78% nitrogen and 21% oxygen."),
    ("What is the primary power source that fuels our Sun?", ["Coal burning", "Nuclear fission", "Nuclear fusion", "Chemical combustion"], 2, "Simple Science", "The Sun fuses hydrogen nuclei into helium in its core."),
    # More GK & Landmarks
    ("Who invented the modern telephone?", ["Thomas Edison", "Alexander Graham Bell", "Nikola Tesla", "Guglielmo Marconi"], 1, "World GK", "Alexander Graham Bell was awarded the telephone patent in 1876."),
    ("Which is the largest island in the world?", ["Madagascar", "Greenland", "Borneo", "New Guinea"], 1, "World GK", "Greenland is the world's largest non-continental island."),
    ("How many players are on the field in a standard cricket team?", ["9", "10", "11", "12"], 2, "World GK", "A cricket team fields 11 players at a time."),
    ("How many rings make up the Olympic symbol?", ["4", "5", "6", "7"], 1, "World GK", "The Olympic symbol has 5 interlocking colored rings representing continents."),
    ("In which country is the famous Leaning Tower of Pisa?", ["France", "Spain", "Italy", "Austria"], 2, "Landmarks", "The Leaning Tower of Pisa is located in Tuscany, Italy."),
    ("What is the official language of Brazil?", ["Spanish", "Portuguese", "French", "English"], 1, "Countries & Capitals", "Brazil is the only Portuguese-speaking nation in South America."),
    ("What is the capital of South Korea?", ["Tokyo", "Seoul", "Busan", "Pyongyang"], 1, "Countries & Capitals", "Seoul is the capital and largest metropolis of South Korea."),
    ("What is the capital of Egypt?", ["Alexandria", "Cairo", "Luxor", "Giza"], 1, "Countries & Capitals", "Cairo, situated on the Nile, is the historic capital of Egypt."),
    ("Which organ cleans and filters waste products from human blood?", ["Heart", "Lungs", "Kidneys", "Stomach"], 2, "Simple Science", "Kidneys filter waste and extra water from blood to make urine."),
    ("What type of lens is used to correct nearsightedness (myopia)?", ["Convex lens", "Concave lens", "Bifocal lens", "Cylindrical only"], 1, "Simple Science", "Diverging concave lenses are used to correct nearsightedness."),
    ("What is the capital of Meghalaya?", ["Shillong", "Aizawl", "Imphal", "Kohima"], 0, "Indian States", "Shillong, known as Scotland of the East, is Meghalaya's capital."),
    ("What is the capital of Nagaland?", ["Dimapur", "Kohima", "Mokokchung", "Tuensang"], 1, "Indian States", "Kohima is the capital city of Nagaland."),
    ("What is 13 x 7?", ["81", "87", "91", "97"], 2, "Basic Math", "13 x 7 = 91."),
    ("What is 250 divided by 5?", ["40", "45", "50", "60"], 2, "Basic Math", "250 / 5 = 50."),
    ("How many sides does a decagon have?", ["8", "9", "10", "12"], 2, "Shapes & Colors", "A decagon is a polygon with 10 sides."),
    ("What is the capital of Tripura?", ["Agartala", "Udaipur", "Dharmanagar", "Kailashahar"], 0, "Indian States", "Agartala is the capital city of Tripura.")
]

for i, (q, opts, cidx, cat, exp) in enumerate(kids_hard_data, 1):
    add_q(kids_questions, f"k_hard_{i:03d}", q, opts, cidx, cat, "Hard", exp)


# ==========================================
# ADULTS QUESTIONS (180 Total: 60 Easy, 60 Med, 60 Hard)
# ==========================================

# --- ADULTS EASY (60) ---
adults_easy_data = [
    # Math & Logic
    ("What is 25% of 240?", ["50", "60", "70", "80"], 1, "Quantitative Math", "25% is one-fourth: 240 / 4 = 60."),
    ("What is the average of 10, 20, 30, and 40?", ["20", "25", "30", "35"], 1, "Quantitative Math", "Sum = 100. Average = 100 / 4 = 25."),
    ("If a shirt costs $80 and is discounted by 20%, what is the sale price?", ["$60", "$64", "$68", "$72"], 1, "Quantitative Math", "20% discount = $16. Sale price = 80 - 16 = $64."),
    ("Solve for x: 3x + 9 = 24.", ["3", "5", "7", "9"], 1, "Algebra & Logic", "3x = 24 - 9 = 15; x = 5."),
    ("What is the ratio of 45 minutes to 1 hour?", ["3:4", "2:3", "4:5", "1:2"], 0, "Quantitative Math", "45 mins : 60 mins simplifies by 15 to 3:4."),
    ("What is 15% of 500?", ["65", "70", "75", "80"], 2, "Quantitative Math", "10% is 50, 5% is 25. 50 + 25 = 75."),
    ("A car travels 180 km in 3 hours. What is its average speed?", ["50 km/h", "60 km/h", "70 km/h", "80 km/h"], 1, "Quantitative Math", "Speed = Distance / Time = 180 / 3 = 60 km/h."),
    ("What is the next number in series: 2, 4, 8, 16, __?", ["24", "30", "32", "36"], 2, "Algebra & Logic", "Each term doubles: 16 x 2 = 32."),
    # Indian History & Freedom Movement
    ("In which year did India gain independence from British rule?", ["1945", "1947", "1948", "1950"], 1, "Indian History", "India achieved independence on August 15, 1947."),
    ("Who was the first Prime Minister of independent India?", ["Mahatma Gandhi", "Jawaharlal Nehru", "Sardar Patel", "Dr. B.R. Ambedkar"], 1, "Indian History", "Jawaharlal Nehru served as India's first Prime Minister from 1947 to 1964."),
    ("Who is known as the 'Father of the Indian Constitution'?", ["Dr. Rajendra Prasad", "Dr. B.R. Ambedkar", "Jawaharlal Nehru", "B.N. Rau"], 1, "Indian Polity", "Dr. Bhimrao Ramji Ambedkar chaired the Drafting Committee."),
    ("Who was called the 'Iron Man of India'?", ["Subhash Chandra Bose", "Sardar Vallabhbhai Patel", "Bhagat Singh", "Lal Bahadur Shastri"], 1, "Indian History", "Sardar Patel unified over 560 princely states into the Indian Union."),
    ("The famous 'Dandi March' led by Mahatma Gandhi was related to which tax?", ["Cotton Tax", "Salt Tax", "Land Tax", "Indigo Tax"], 1, "Indian History", "The 1930 Salt Satyagraha protested the oppressive British salt monopoly."),
    ("In which year was the Quit India Movement launched?", ["1938", "1940", "1942", "1945"], 2, "Indian History", "Mahatma Gandhi launched Quit India on 8 August 1942 in Bombay."),
    # Indian Polity & Constitution
    ("How many Fundamental Rights are currently recognized by the Indian Constitution?", ["5", "6", "7", "8"], 1, "Indian Polity", "There are 6 Fundamental Rights (Right to Property was removed by the 44th Amendment)."),
    ("What is the minimum voting age for Indian citizens?", ["16", "18", "21", "25"], 1, "Indian Polity", "The 61st Constitutional Amendment lowered the voting age to 18 in 1988."),
    ("Who is the Supreme Commander of the Indian Armed Forces?", ["Prime Minister", "Defense Minister", "President of India", "Chief of Defense Staff"], 2, "Indian Polity", "The President of India is the constitutional Commander-in-Chief."),
    ("What is the tenure of a member of the Rajya Sabha?", ["4 years", "5 years", "6 years", "Permanent without term"], 2, "Indian Polity", "Rajya Sabha members are elected for 6-year terms; 1/3rd retire every 2 years."),
    ("Which article of the Indian Constitution deals with the Right to Equality?", ["Articles 14–18", "Articles 19–22", "Articles 25–28", "Article 32"], 0, "Indian Polity", "Articles 14 through 18 guarantee equality before the law and prohibit discrimination."),
    # World History & Geography
    ("Which ancient wonder was located in Alexandria, Egypt?", ["Hanging Gardens", "Lighthouse of Alexandria", "Colossus of Rhodes", "Temple of Artemis"], 1, "World History", "The Pharos (Lighthouse) of Alexandria guided Mediterranean ships."),
    ("In which year did World War II officially end?", ["1943", "1944", "1945", "1948"], 2, "World History", "World War II concluded in 1945 with Allied victory."),
    ("What is the longest continental mountain range in the world?", ["Himalayas", "Rockies", "Andes", "Alps"], 2, "World Geography", "The Andes range in South America spans over 7,000 kilometers."),
    ("Which city is known as the 'City of Canals'?", ["Amsterdam", "Venice", "Bruges", "Stockholm"], 1, "World Geography", "Venice, Italy is famous for its intricate network of waterways."),
    ("What is the capital city of Australia?", ["Sydney", "Melbourne", "Canberra", "Perth"], 2, "World Geography", "Canberra was founded in 1913 as Australia's federal capital."),
    ("Which country is known as the 'Land of the Rising Sun'?", ["China", "Japan", "South Korea", "Thailand"], 1, "World Geography", "Japan is called Nihon/Nippon, meaning 'origin of the sun'."),
    # Science & Technology
    ("What is the primary semiconductor material used in modern computer chips?", ["Copper", "Silicon", "Carbon", "Gallium"], 1, "Science & Tech", "Silicon's valence and abundance make it the cornerstone of semiconductors."),
    ("What does 'HTTP' stand for in web browsing?", ["HyperText Transfer Protocol", "High Transmission Text Program", "Hyperlink Text Test Path", "Home Terminal Transfer Portal"], 0, "Science & Tech", "HTTP is the application protocol that powers data transfer on the World Wide Web."),
    ("Who formulated the three laws of classical motion?", ["Albert Einstein", "Isaac Newton", "Galileo Galilei", "Johannes Kepler"], 1, "Science & Tech", "Sir Isaac Newton published his laws of motion in the Principia in 1687."),
    ("What is the powerhouse organelle of eukaryotic cells?", ["Endoplasmic Reticulum", "Mitochondria", "Golgi Apparatus", "Lysosome"], 1, "Science & Tech", "Mitochondria generate most of the cell's supply of ATP energy."),
    ("Which blood group is known as the universal donor for red blood cells?", ["AB Positive", "O Negative", "A Positive", "B Negative"], 1, "Science & Tech", "O Negative blood lacks A, B, and Rh antigens, making it safe for anyone."),
    ("What does CPU stand for in computer hardware?", ["Central Process Unit", "Central Processing Unit", "Core Power Unit", "Computer Program Utility"], 1, "Science & Tech", "The CPU carries out instructions of computer programs."),
    ("Which gas is the most abundant in Earth's atmosphere?", ["Oxygen", "Nitrogen", "Carbon Dioxide", "Argon"], 1, "Science & Tech", "Nitrogen accounts for approximately 78.08% of Earth's air."),
    # GK & Economics
    ("What does 'GDP' stand for in economic terms?", ["Gross Domestic Product", "General Domestic Profit", "Global Development Price", "Gross Division Production"], 0, "General Knowledge", "Gross Domestic Product measures monetary market value of all final goods produced."),
    ("Which institution acts as the central bank of India?", ["State Bank of India", "Reserve Bank of India", "HDFC Bank", "NITI Aayog"], 1, "Indian Polity", "The Reserve Bank of India (RBI) controls monetary policy and currency issuance."),
    ("Where are the headquarters of the United Nations located?", ["Geneva", "Paris", "New York City", "London"], 2, "General Knowledge", "The UN headquarters are situated in Manhattan, New York City."),
    ("Who wrote the Indian national anthem 'Jana Gana Mana'?", ["Bankim Chandra Chatterjee", "Rabindranath Tagore", "Sarojini Naidu", "Sri Aurobindo"], 1, "Indian History", "Rabindranath Tagore composed both India's and Bangladesh's national anthems."),
    ("Which is the largest public sector commercial bank in India?", ["Punjab National Bank", "State Bank of India", "Bank of Baroda", "Canara Bank"], 1, "General Knowledge", "State Bank of India (SBI) is India's largest public sector bank."),
    ("What is 12% of 300?", ["24", "36", "42", "48"], 1, "Quantitative Math", "12 x 3 = 36."),
    ("What is the value of 2 to the power of 5 (2⁵)?", ["16", "32", "64", "128"], 1, "Quantitative Math", "2 x 2 x 2 x 2 x 2 = 32."),
    ("If the ratio of boys to girls in a class of 40 is 3:2, how many boys are there?", ["16", "20", "24", "28"], 2, "Quantitative Math", "3/5 of 40 = 3 x 8 = 24 boys."),
    ("Who was the second Prime Minister of India, known for 'Jai Jawan Jai Kisan'?", ["Morarji Desai", "Lal Bahadur Shastri", "Indira Gandhi", "Charan Singh"], 1, "Indian History", "Lal Bahadur Shastri coined this slogan during the 1965 war."),
    ("Which river is known as the 'Ganga of the South'?", ["Krishna", "Godavari", "Cauvery (Kaveri)", "Narmada"], 1, "Indian Geography", "The Godavari is known as Dakshin Ganga due to its scale and spiritual reverence."),
    ("In which Indian state is the Kaziranga National Park located?", ["West Bengal", "Assam", "Manipur", "Arunachal Pradesh"], 1, "Indian Geography", "Kaziranga in Assam is home to the world's largest population of great one-horned rhinos."),
    ("Who is considered the author of the epic 'Mahabharata'?", ["Valmiki", "Ved Vyasa", "Kalidasa", "Tulsidas"], 1, "Indian History", "Sage Ved Vyasa is credited with composing the epic Mahabharata."),
    ("Which planet has the shortest day (fastest rotation) in our solar system?", ["Mercury", "Jupiter", "Saturn", "Earth"], 1, "Science & Tech", "Jupiter rotates in just under 10 hours despite its massive size."),
    ("What is the chemical formula for ordinary table salt?", ["KCl", "NaCl", "NaOH", "CaCl₂"], 1, "Science & Tech", "Sodium chloride (NaCl) is common table salt."),
    ("Which Indian state has the highest literacy rate according to census data?", ["Tamil Nadu", "Goa", "Kerala", "Mizoram"], 2, "Indian Geography", "Kerala consistently leads India with literacy rates exceeding 94%."),
    ("Which organ in the human body produces insulin?", ["Liver", "Pancreas", "Kidney", "Gallbladder"], 1, "Science & Tech", "The beta cells in the islets of Langerhans in the pancreas secrete insulin."),
    ("What is the capital of Canada?", ["Toronto", "Montreal", "Ottawa", "Calgary"], 2, "World Geography", "Ottawa is Canada's capital city in Ontario."),
    ("What is the currency of the United Kingdom?", ["Euro", "Pound Sterling", "Dollar", "Franc"], 1, "General Knowledge", "The UK uses the British Pound Sterling (£)."),
    ("Who discovered penicillin, the first true antibiotic?", ["Louis Pasteur", "Alexander Fleming", "Robert Koch", "Edward Jenner"], 1, "Science & Tech", "Alexander Fleming discovered penicillin from Penicillium mold in 1928."),
    ("What is the square of 14?", ["169", "186", "196", "216"], 2, "Quantitative Math", "14 x 14 = 196."),
    ("Solve for y: 5y - 15 = 35.", ["8", "10", "12", "14"], 1, "Algebra & Logic", "5y = 50; y = 10."),
    ("How many degrees are in the angles of a triangle combined?", ["90°", "180°", "270°", "360°"], 1, "Quantitative Math", "The sum of interior angles in any Euclidean triangle is 180 degrees."),
    ("What is the currency of South Africa?", ["Shilling", "Rand", "Pula", "Kwacha"], 1, "General Knowledge", "The official currency of South Africa is the South African Rand (ZAR)."),
    ("Which element has the chemical symbol 'O'?", ["Osmium", "Oxygen", "Gold", "Lead"], 1, "Science & Tech", "O represents Oxygen with atomic number 8."),
    ("Which Indian state is famous for the classical dance form 'Kathakali'?", ["Karnataka", "Tamil Nadu", "Kerala", "Andhra Pradesh"], 2, "Indian Geography", "Kathakali originated in Kerala with elaborate makeup and costumes."),
    ("What is the boiling point of water in Fahrenheit at sea level?", ["100°F", "180°F", "212°F", "250°F"], 2, "Science & Tech", "Water boils at 212°F (100°C)."),
    ("Who was the Mughal Emperor who built the Red Fort in Delhi?", ["Akbar", "Jahangir", "Shah Jahan", "Aurangzeb"], 2, "Indian History", "Shah Jahan commissioned the Red Fort in 1638 when shifting capital to Shahjahanabad."),
    ("What is the value of Pi (π) rounded to two decimal places?", ["3.12", "3.14", "3.16", "3.18"], 1, "Quantitative Math", "Pi is approximately 3.14159...")
]

for i, (q, opts, cidx, cat, exp) in enumerate(adults_easy_data, 1):
    add_q(adults_questions, f"a_easy_{i:03d}", q, opts, cidx, cat, "Easy", exp)

# --- ADULTS MEDIUM (60) ---
adults_med_data = [
    # Math & Logic
    ("If an item is sold for $120 with a 20% profit on cost price, what was the cost price?", ["$96", "$100", "$105", "$110"], 1, "Quantitative Math", "Selling Price = 1.20 x Cost. Cost = 120 / 1.20 = $100."),
    ("What is the average of the first 5 prime numbers (2, 3, 5, 7, 11)?", ["5.2", "5.6", "6.0", "6.4"], 1, "Quantitative Math", "Sum = 28. Average = 28 / 5 = 5.6."),
    ("If 6 workers take 8 hours to complete a task, how many hours will 4 workers take at the same rate?", ["10 hours", "12 hours", "14 hours", "16 hours"], 1, "Quantitative Math", "Total work = 6 x 8 = 48 worker-hours. Time for 4 workers = 48 / 4 = 12 hours."),
    ("What is the next number in series: 2, 6, 12, 20, 30, __?", ["40", "42", "44", "46"], 1, "Algebra & Logic", "Differences are 4, 6, 8, 10. Next difference is 12: 30 + 12 = 42 (n² + n)."),
    ("If a card is drawn from a standard deck of 52 cards, what is the probability of drawing an Ace?", ["1/13", "1/26", "1/52", "4/13"], 0, "Algebra & Logic", "There are 4 Aces: 4/52 = 1/13."),
    ("What is the compound interest on $1,000 for 2 years at 10% per annum compounded annually?", ["$200", "$210", "$220", "$250"], 1, "Quantitative Math", "Amount = 1000 x (1.10)² = $1,210. Interest = 1210 - 1000 = $210."),
    ("A train 150 meters long crosses a pole in 15 seconds. What is its speed in km/h?", ["30 km/h", "36 km/h", "40 km/h", "45 km/h"], 1, "Quantitative Math", "Speed = 150m / 15s = 10 m/s. 10 x 3.6 = 36 km/h."),
    ("If log₁₀(x) = 3, what is the value of x?", ["30", "100", "300", "1000"], 3, "Algebra & Logic", "10³ = 1,000."),
    # Indian History & Freedom Movement
    ("Who founded the Arya Samaj in 1875?", ["Raja Ram Mohan Roy", "Swami Dayananda Saraswati", "Swami Vivekananda", "Ishwar Chandra Vidyasagar"], 1, "Indian History", "Swami Dayananda Saraswati established the Arya Samaj in Bombay."),
    ("In which session of Congress was the 'Purna Swaraj' (Complete Independence) resolution adopted?", ["1920 Nagpur", "1929 Lahore", "1931 Karachi", "1936 Lucknow"], 1, "Indian History", "The 1929 Lahore session presided over by Jawaharlal Nehru passed Purna Swaraj."),
    ("Who was the Viceroy of India during the 1905 Partition of Bengal?", ["Lord Curzon", "Lord Ripon", "Lord Minto", "Lord Dalhousie"], 0, "Indian History", "Lord Curzon partitioned Bengal in 1905 on administrative pretext."),
    ("The famous Battle of Plassey was fought in which year?", ["1757", "1761", "1764", "1773"], 0, "Indian History", "Robert Clive defeated Siraj-ud-Daulah at Plassey on June 23, 1757."),
    ("Which ancient Indian king embraced Buddhism after the bloody Kalinga War?", ["Chandragupta Maurya", "Ashoka", "Samudragupta", "Harshavardhana"], 1, "Indian History", "Emperor Ashoka renounced conquest by war in favor of Dhamma Vijaya."),
    ("Who was the founder of the Maurya Empire?", ["Chandragupta Maurya", "Bindusara", "Ashoka", "Brihadratha"], 0, "Indian History", "Chandragupta Maurya founded the dynasty with guidance from Chanakya."),
    # Indian Polity & Constitution
    ("Which constitutional amendment introduced the Goods and Services Tax (GST) in India?", ["100th", "101st", "102nd", "103rd"], 1, "Indian Polity", "The 101st Amendment Act, 2016 introduced comprehensive nationwide GST."),
    ("Under which Article of the Constitution can the President proclaim a Financial Emergency?", ["Article 352", "Article 356", "Article 360", "Article 368"], 2, "Indian Polity", "Article 360 allows declaration of financial emergency (never invoked in India)."),
    ("Which writ literally means 'to have the body of'?", ["Mandamus", "Habeas Corpus", "Quo-Warranto", "Certiorari"], 1, "Indian Polity", "Habeas Corpus protects individuals against unlawful and arbitrary detention."),
    ("Which schedule of the Indian Constitution lists the officially recognized languages?", ["Seventh Schedule", "Eighth Schedule", "Ninth Schedule", "Eleventh Schedule"], 1, "Indian Polity", "The Eighth Schedule currently enumerates 22 scheduled languages."),
    ("Who was the constitutional advisor to the Constituent Assembly of India?", ["B.R. Ambedkar", "B.N. Rau", "K.M. Munshi", "Sachchidananda Sinha"], 1, "Indian Polity", "Sir Benegal Narsing Rau prepared the initial constitutional draft."),
    # World Geography & History
    ("Which canal connects the Mediterranean Sea directly to the Red Sea?", ["Panama Canal", "Suez Canal", "Kiel Canal", "Corinth Canal"], 1, "World Geography", "The Suez Canal in Egypt opened in 1869, shortening maritime trade routes."),
    ("The Renaissance period originated in which European country during the 14th century?", ["France", "England", "Italy", "Germany"], 2, "World History", "The Renaissance began in Italian city-states like Florence and Venice."),
    ("Which is the largest landlocked country in the world by surface area?", ["Mongolia", "Kazakhstan", "Bolivia", "Chad"], 1, "World Geography", "Kazakhstan covers 2.72 million km² with no access to open world oceans."),
    ("The historic 1215 document 'Magna Carta' was signed in which country?", ["France", "England", "Scotland", "Germany"], 1, "World History", "King John of England signed the Magna Carta at Runnymede in 1215."),
    ("Which strait separates mainland Asia from North America?", ["Bering Strait", "Cook Strait", "Strait of Magellan", "Bosphorus Strait"], 0, "World Geography", "The Bering Strait connects the Arctic Ocean to the Bering Sea between Russia and Alaska."),
    ("What is the capital of Turkey?", ["Istanbul", "Ankara", "Izmir", "Antalya"], 1, "World Geography", "Ankara is the capital of Turkey, while Istanbul is its largest city."),
    # Science & Technology
    ("What does 'DNA' stand for in molecular biology?", ["Deoxyribonucleic Acid", "Diribonucleic Acid", "Deoxyribose Nitrogen Acid", "Dual Nucleic Atom"], 0, "Science & Tech", "DNA is the hereditary macromolecule carrying genetic instructions."),
    ("Which subatomic particle carries a negative electrical charge?", ["Proton", "Neutron", "Electron", "Positron"], 2, "Science & Tech", "Electrons orbit the nucleus with a negative elementary charge."),
    ("What is the SI unit of electric current?", ["Volt", "Ampere", "Ohm", "Watt"], 1, "Science & Tech", "The Ampere (A) measures the rate of electric charge flow."),
    ("Which space telescope was launched in December 2021 to succeed Hubble?", ["Kepler", "James Webb Space Telescope", "Spitzer", "Chandra"], 1, "Science & Tech", "JWST observes in infrared with a 6.5-meter gold-coated primary mirror."),
    ("What does 'GPU' stand for in modern computing and AI?", ["General Processing Unit", "Graphics Processing Unit", "Grid Parallel Unit", "Graphical Power Utility"], 1, "Science & Tech", "GPUs excel at massive parallel computations essential for 3D and AI."),
    ("What is the chemical name for laughing gas?", ["Nitrogen dioxide", "Nitrous oxide", "Nitric oxide", "Dinitrogen tetroxide"], 1, "Science & Tech", "Nitrous oxide (N₂O) produces mild euphoria and analgesic effects."),
    ("In computer networking, what protocol resolves domain names to IP addresses?", ["DHCP", "DNS", "FTP", "SNMP"], 1, "Science & Tech", "The Domain Name System (DNS) translates human URLs into numerical IP addresses."),
    # Indian Geography & Environment
    ("Which is the highest peak in the Western Ghats of India?", ["Doda Betta", "Anamudi", "Kalsubai", "Mullayanagiri"], 1, "Indian Geography", "Anamudi in Kerala stands at 2,695 meters as South India's highest peak."),
    ("The Tropic of Cancer passes through how many Indian states?", ["6", "7", "8", "9"], 2, "Indian Geography", "It passes through 8 states: Gujarat, Rajasthan, MP, Chhattisgarh, Jharkhand, WB, Tripura, Mizoram."),
    ("Which Indian state produces the largest quantity of tea in the country?", ["Kerala", "West Bengal", "Assam", "Tamil Nadu"], 2, "Indian Geography", "Assam produces over 50% of India's total annual tea harvest."),
    ("The famous 'Silent Valley National Park' is located in which Indian state?", ["Tamil Nadu", "Karnataka", "Kerala", "Uttarakhand"], 2, "Indian Geography", "Silent Valley in the Nilgiri hills of Kerala is renowned for tropical rainforests."),
    ("Which river originates from the Mansarovar Lake region in Tibet?", ["Ganga", "Brahmaputra", "Godavari", "Mahanadi"], 1, "Indian Geography", "The Brahmaputra (known as Yarlung Tsangpo in Tibet) rises near Mansarovar."),
    # GK, Economics & World Affairs
    ("Where are the headquarters of the International Monetary Fund (IMF) situated?", ["Geneva", "London", "Washington, D.C.", "New York City"], 2, "General Knowledge", "The IMF and World Bank are headquartered in Washington, D.C."),
    ("Who wrote the foundational economics treatise 'The Wealth of Nations' (1776)?", ["John Maynard Keynes", "Adam Smith", "Karl Marx", "Milton Friedman"], 1, "General Knowledge", "Adam Smith is widely regarded as the father of modern economics."),
    ("The Nobel Prize in Economics is officially named after which entity?", ["Alfred Nobel directly", "Sveriges Riksbank", "Bank of England", "Norwegian Parliament"], 1, "General Knowledge", "It is the Sveriges Riksbank Prize in Economic Sciences in Memory of Alfred Nobel."),
    ("Which country has the highest number of time zones spanning its territory?", ["Russia", "United States", "France", "China"], 2, "World Geography", "France spans 12 time zones due to its overseas territories around the globe."),
    ("What is the term for a market structure dominated by only a few large sellers?", ["Monopoly", "Monopsony", "Oligopoly", "Perfect Competition"], 2, "General Knowledge", "An oligopoly features a small number of interdependent commercial producers."),
    ("What is the square root of 225?", ["13", "14", "15", "16"], 2, "Quantitative Math", "15 x 15 = 225."),
    ("Solve for x: x² - 9 = 0 (positive root).", ["2", "3", "4", "9"], 1, "Algebra & Logic", "x² = 9, so positive root is 3."),
    ("What is the value of 5! (5 factorial)?", ["60", "100", "120", "150"], 2, "Quantitative Math", "5 x 4 x 3 x 2 x 1 = 120."),
    ("Who was the founder of the Sikh faith and its first Guru?", ["Guru Nanak Dev", "Guru Gobind Singh", "Guru Arjan Dev", "Guru Tegh Bahadur"], 0, "Indian History", "Guru Nanak Dev founded Sikhism in the 15th century in Punjab."),
    ("Which layer of the atmosphere contains the majority of weather phenomena?", ["Stratosphere", "Troposphere", "Mesosphere", "Thermosphere"], 1, "Science & Tech", "The troposphere extends 8–15 km up and contains almost all atmospheric water vapor."),
    ("What is the primary function of platelets in human blood?", ["Oxygen transport", "Blood clotting", "Antibody production", "Hormone secretion"], 1, "Science & Tech", "Platelets (thrombocytes) aggregate to plug wounds and clot blood."),
    ("Which treaty ended World War I in 1919?", ["Treaty of Paris", "Treaty of Versailles", "Treaty of Ghent", "Treaty of Utrecht"], 1, "World History", "The Treaty of Versailles was signed at the Paris Peace Conference in June 1919."),
    ("What is the capital of Switzerland?", ["Zurich", "Geneva", "Bern", "Basel"], 2, "World Geography", "Bern is the de facto federal city and capital of Switzerland."),
    ("What is the half-life of Carbon-14 used in archaeological radiocarbon dating?", ["~1,250 years", "~5,730 years", "~10,500 years", "~24,000 years"], 1, "Science & Tech", "Carbon-14 decays with a half-life of approximately 5,730 ± 40 years."),
    ("Which Indian state has the largest forest cover by total area?", ["Arunachal Pradesh", "Madhya Pradesh", "Chhattisgarh", "Odisha"], 1, "Indian Geography", "Madhya Pradesh has the largest total forested area in India."),
    ("What is the SI unit of pressure?", ["Pascal", "Joule", "Newton", "Bar"], 0, "Science & Tech", "The Pascal (Pa) is one Newton per square meter."),
    ("What does 'URL' stand for in internet navigation?", ["Uniform Resource Locator", "Universal Reference Link", "Unified Routing Location", "User Record Locator"], 0, "Science & Tech", "URL specifies the web address of a digital resource."),
    ("Who was the first woman Prime Minister in world history?", ["Indira Gandhi", "Sirimavo Bandaranaike", "Margaret Thatcher", "Golda Meir"], 1, "World History", "Sirimavo Bandaranaike became Prime Minister of Sri Lanka (Ceylon) in 1960."),
    ("In which year was the Reserve Bank of India (RBI) established?", ["1935", "1947", "1950", "1955"], 0, "Indian Polity", "RBI was set up on April 1, 1935 under the Reserve Bank of India Act, 1934."),
    ("What is the chemical formula for ozone?", ["O₂", "O₃", "O₄", "CO₂"], 1, "Science & Tech", "Ozone consists of three bound oxygen atoms (O₃)."),
    ("Which is the deepest freshwater lake in the world?", ["Lake Superior", "Lake Baikal", "Lake Victoria", "Lake Tanganyika"], 1, "World Geography", "Lake Baikal in Siberia plunges to a maximum depth of 1,642 meters."),
    ("What is the acceleration due to gravity on Earth's surface approximately?", ["8.8 m/s²", "9.8 m/s²", "10.8 m/s²", "12.0 m/s²"], 1, "Science & Tech", "Standard gravity 'g' is approximately 9.80665 m/s²."),
    ("What is the tenure of the Chief Election Commissioner of India?", ["5 years or age 60", "6 years or age 65", "4 years or age 62", "5 years or age 65"], 1, "Indian Polity", "The CEC holds office for 6 years or until reaching 65 years of age.")
]

for i, (q, opts, cidx, cat, exp) in enumerate(adults_med_data, 1):
    add_q(adults_questions, f"a_med_{i:03d}", q, opts, cidx, cat, "Medium", exp)

# --- ADULTS HARD (60) ---
adults_hard_data = [
    # Math, Probability & Logic
    ("Two dice are rolled simultaneously. What is the probability that the sum of the numbers is 7?", ["1/12", "1/6", "5/36", "7/36"], 1, "Algebra & Logic", "Favorable outcomes are (1,6), (2,5), (3,4), (4,3), (5,2), (6,1) = 6. 6/36 = 1/6."),
    ("If x + 1/x = 4, what is the value of x² + 1/x²?", ["12", "14", "16", "18"], 1, "Algebra & Logic", "(x + 1/x)² = x² + 2 + 1/x² = 16. Therefore, x² + 1/x² = 16 - 2 = 14."),
    ("A sum of money doubles itself in 8 years at simple interest. What is the annual rate of interest?", ["10%", "12.5%", "15%", "16.6%"], 1, "Quantitative Math", "Simple Interest = Principal. Rate = (100 x P) / (P x 8) = 100/8 = 12.5%."),
    ("How many different arrangements can be made using all the letters of the word 'LEADER'?", ["360", "720", "180", "120"], 0, "Algebra & Logic", "6 letters with 'E' repeating twice: 6! / 2! = 720 / 2 = 360."),
    ("A pipe can fill a tank in 6 hours, while a drain can empty it in 8 hours. Working together, how long will it take to fill the tank?", ["18 hours", "20 hours", "24 hours", "28 hours"], 2, "Quantitative Math", "Net rate = 1/6 - 1/8 = (4 - 3)/24 = 1/24 per hour. Time = 24 hours."),
    ("What is the remainder when 2⁵⁰ is divided by 7?", ["1", "2", "4", "6"], 2, "Algebra & Logic", "2³ = 8 ≡ 1 (mod 7). 50 = 3 x 16 + 2. (2³)¹⁶ x 2² ≡ 1 x 4 = 4."),
    ("If the roots of the quadratic equation ax² + bx + c = 0 are real and equal, what is the value of the discriminant?", ["b² - 4ac > 0", "b² - 4ac = 0", "b² - 4ac < 0", "b² = 2ac"], 1, "Algebra & Logic", "A discriminant equal to 0 produces exactly two identical real roots."),
    ("In a group of 50 people, 30 like tea, 25 like coffee, and 10 like both. How many like neither?", ["3", "5", "8", "10"], 1, "Algebra & Logic", "Union = 30 + 25 - 10 = 45. Neither = 50 - 45 = 5."),
    # Indian Polity & Constitutional Law
    ("The 'Basic Structure Doctrine' of the Indian Constitution was established in which landmark Supreme Court case?", ["Golaknath (1967)", "Kesavananda Bharati (1973)", "Minerva Mills (1980)", "Maneka Gandhi (1978)"], 1, "Indian Polity", "The 13-judge bench in Kesavananda Bharati ruled that Parliament cannot alter the basic structure."),
    ("Under which Article of the Constitution can Parliament amend the Constitution?", ["Article 356", "Article 368", "Article 370", "Article 371"], 1, "Indian Polity", "Article 368 governs the power and constituent procedure of Parliament to amend."),
    ("Which constitutional amendment deleted the Right to Property from the list of Fundamental Rights?", ["42nd Amendment (1976)", "44th Amendment (1978)", "52nd Amendment (1985)", "73rd Amendment (1992)"], 1, "Indian Polity", "The 44th Amendment Act made property a legal right under Article 300A."),
    ("Which committee recommended the establishment of the Panchayati Raj system in India with a 3-tier model?", ["Sarkaria Commission", "Balwant Rai Mehta Committee", "Verma Committee", "Kothari Commission"], 1, "Indian Polity", "The 1957 Balwant Rai Mehta committee proposed Gram, Block, and District tiers."),
    ("The Directive Principles of State Policy in the Indian Constitution were borrowed from which country's constitution?", ["United States", "Irish Free State (Ireland)", "Soviet Union", "Australia"], 1, "Indian Polity", "India's DPSPs (Part IV) were inspired by Ireland's 1937 Constitution."),
    ("What is the quorum required to constitute a meeting of either House of the Indian Parliament?", ["1/5th of total members", "1/10th of total members", "1/8th of total members", "1/6th of total members"], 1, "Indian Polity", "Article 100(3) sets quorum at 10% (one-tenth) of total membership."),
    # Indian History & Freedom Struggle
    ("Who was the British Governor-General who abolished the practice of Sati in India in 1829?", ["Lord Wellesley", "Lord William Bentinck", "Lord Cornwallis", "Lord Canning"], 1, "Indian History", "Lord William Bentinck enacted Regulation XVII with advocacy from Raja Ram Mohan Roy."),
    ("The famous Lucknow Pact of 1916 was an agreement between which two organizations?", ["Congress and Muslim League", "Moderates and Extremists", "Congress and British Viceroy", "Ghadar Party and Congress"], 0, "Indian History", "It established joint nationalist demands between the Indian National Congress and the Muslim League."),
    ("Who organized the 'Hindustan Socialist Republican Association' (HSRA) in 1928 at Feroz Shah Kotla?", ["Chandrashekhar Azad & Bhagat Singh", "Subhash Chandra Bose", "Lala Lajpat Rai", "V.D. Savarkar"], 0, "Indian History", "Azad, Bhagat Singh, and comrades reorganized the HRA into the HSRA."),
    ("Who was the court poet of King Harshavardhana and author of the 'Harshacharita'?", ["Kalidasa", "Banabhatta", "Bhavabhuti", "Harisena"], 1, "Indian History", "Banabhatta composed both Harshacharita and Kadambari in 7th century Sanskrit."),
    ("The Indus Valley Civilization site 'Lothal', famous for its ancient tidal dockyard, is situated in which state?", ["Rajasthan", "Gujarat", "Haryana", "Punjab"], 1, "Indian History", "Lothal was a prominent port and bead-making center in modern Gujarat."),
    # World History & Treaties
    ("The historic 1648 'Peace of Westphalia' is credited with establishing which major geopolitical concept?", ["Universal Human Rights", "Sovereign Nation-States", "Free Trade Zones", "Nuclear Non-Proliferation"], 1, "World History", "Westphalian sovereignty recognized independent state borders and domestic authority."),
    ("The Meiji Restoration of 1868 occurred in which nation, leading to rapid modernization?", ["China", "Japan", "Korea", "Thailand"], 1, "World History", "The Meiji era returned imperial rule and transformed Japan into an industrial power."),
    ("Which battle in 1815 marked the definitive military defeat of Napoleon Bonaparte?", ["Battle of Austerlitz", "Battle of Waterloo", "Battle of Leipzig", "Battle of Trafalgar"], 1, "World History", "The Duke of Wellington and Blücher defeated Napoleon near Waterloo, Belgium."),
    ("The Bretton Woods Conference of 1944 established which two major global institutions?", ["UN and WHO", "IMF and World Bank (IBRD)", "WTO and OECD", "NATO and Warsaw Pact"], 1, "World History", "Bretton Woods created the modern post-war international monetary order."),
    # Science, Quantum & Deep Tech
    ("What fundamental quantum principle states that you cannot simultaneously measure position and momentum with arbitrary precision?", ["Pauli Exclusion Principle", "Heisenberg Uncertainty Principle", "Schrödinger Equation", "Fermi-Dirac Statistic"], 1, "Science & Tech", "Δx · Δp ≥ ℏ/2 establishes the intrinsic limit of quantum measurement."),
    ("What does 'CRISPR-Cas9' technology primarily enable in biomedical science?", ["Stem cell cloning", "Targeted Gene Editing", "Protein crystallization", "MRI brain imaging"], 1, "Science & Tech", "CRISPR-Cas9 provides RNA-guided molecular scissors to modify DNA sequences precisely."),
    ("What is the basic unit of information in a quantum computer?", ["Bit", "Qubit", "Byte", "Trubit"], 1, "Science & Tech", "A qubit exploits quantum superposition to represent 0, 1, or both simultaneously."),
    ("Who is widely regarded as the 'Father of Artificial Intelligence' after organizing the 1956 Dartmouth workshop?", ["Alan Turing", "John McCarthy", "Marvin Minsky", "Claude Shannon"], 1, "Science & Tech", "John McCarthy coined 'Artificial Intelligence' and developed the Lisp language."),
    ("Which Nobel laureate deduced the double-helix structure of DNA alongside James Watson in 1953?", ["Francis Crick", "Linus Pauling", "Maurice Wilkins", "Erwin Chargaff"], 0, "Science & Tech", "Watson and Crick published the DNA structure relying on Rosalind Franklin's Photo 51."),
    ("What is the term for the boundary around a black hole beyond which nothing, not even light, can escape?", ["Ergosphere", "Event Horizon", "Photon Sphere", "Accretion Disk"], 1, "Science & Tech", "The event horizon marks the gravitational point of no return."),
    ("In cryptography, what mathematical foundation underpins the widely used RSA encryption algorithm?", ["Discrete Logarithms", "Difficulty of factoring large prime products", "Elliptic Curve isogenies", "Lattice reduction"], 1, "Science & Tech", "RSA relies on the practical difficulty of factoring large semiprime integers."),
    # World Geography & Earth Sciences
    ("Which African lake is the source of the White Nile?", ["Lake Tanganyika", "Lake Victoria", "Lake Malawi", "Lake Chad"], 1, "World Geography", "Lake Victoria is Africa's largest tropical lake and prime source of the White Nile."),
    ("The 'Ring of Fire' is a horseshoe-shaped basin in which ocean renowned for earthquakes and active volcanoes?", ["Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean"], 2, "World Geography", "The Pacific Ring of Fire contains over 75% of the world's active and dormant volcanoes."),
    ("Which country has the longest coastline in the world?", ["Russia", "Canada", "Norway", "Australia"], 1, "World Geography", "Canada's sprawling archipelago yields over 202,080 km of coastline."),
    ("What geological process occurs when an oceanic tectonic plate dives underneath a continental plate?", ["Rifting", "Subduction", "Transform faulting", "Accretion only"], 1, "World Geography", "Subduction zones create deep ocean trenches and volcanic arcs."),
    ("The 'Danakil Depression', one of the hottest and lowest geological places on Earth, is located in which country?", ["Chad", "Ethiopia", "Algeria", "Namibia"], 1, "World Geography", "Located in the Afar Triangle of Ethiopia, it sits over 100 meters below sea level."),
    # Economics & Global Finance
    ("What does the 'Gini Coefficient' measure in economic analysis?", ["Inflation rate", "Income or wealth inequality", "Trade deficit", "Currency devaluation"], 1, "General Knowledge", "A Gini index of 0 represents complete equality; 1 represents maximal inequality."),
    ("What is the phenomenon where high inflation occurs simultaneously with stagnant economic growth and high unemployment?", ["Deflation", "Stagflation", "Hyperinflation", "Recessionary boom"], 1, "General Knowledge", "Stagflation famously afflicted Western economies during the 1970s oil shocks."),
    ("Which sovereign wealth fund is currently the largest in the world by assets under management?", ["Abu Dhabi Investment Authority", "Government Pension Fund of Norway", "China Investment Corporation", "PIF of Saudi Arabia"], 1, "General Knowledge", "Norway's Oil Fund manages over $1.5 trillion in global assets."),
    ("Which economist advocated government intervention and deficit spending to mitigate recessions in 1936?", ["Friedrich Hayek", "John Maynard Keynes", "Milton Friedman", "David Ricardo"], 1, "General Knowledge", "Keynesian economics was detailed in 'The General Theory of Employment, Interest and Money'."),
    ("In financial markets, what does the 'Sharpe Ratio' measure?", ["Stock liquidity", "Risk-adjusted return of an investment", "Dividend yield growth", "Market beta sensitivity"], 1, "General Knowledge", "Sharpe ratio compares portfolio excess return above the risk-free rate to standard deviation."),
    ("If f(x) = 3x² - 4x + 7, what is the derivative f'(x) evaluated at x = 2?", ["6", "8", "10", "12"], 1, "Algebra & Logic", "f'(x) = 6x - 4. At x = 2: 6(2) - 4 = 12 - 4 = 8."),
    ("What is the determinant of a 2x2 matrix [[3, 5], [2, 4]]?", ["1", "2", "3", "4"], 1, "Algebra & Logic", "ad - bc = (3 x 4) - (5 x 2) = 12 - 10 = 2."),
    ("What is the value of 3⁴ x 2³?", ["324", "486", "648", "720"], 2, "Quantitative Math", "3⁴ = 81; 2³ = 8; 81 x 8 = 648."),
    ("Which Indian scientist won the Nobel Prize in Physics in 1930 for light scattering?", ["Homi Bhabha", "C.V. Raman", "S. Chandrasekhar", "Jagadish Chandra Bose"], 1, "Science & Tech", "Sir C.V. Raman discovered the Raman Effect on February 28, 1928."),
    ("In which year was the first Earth Day celebrated globally?", ["1965", "1970", "1975", "1980"], 1, "General Knowledge", "Gaylord Nelson organized the first Earth Day on April 22, 1970."),
    ("What is the speed of sound in dry air at 20°C approximately?", ["250 m/s", "343 m/s", "420 m/s", "512 m/s"], 1, "Science & Tech", "Sound travels at approximately 343 meters per second in 20°C air."),
    ("Which treaty signed in 1992 formally created the European Union and paved the way for the Euro?", ["Treaty of Rome", "Maastricht Treaty", "Lisbon Treaty", "Treaty of Nice"], 1, "World History", "The Maastricht Treaty established the European Union under three pillars."),
    ("What is the currency of Switzerland?", ["Euro", "Swiss Franc", "Krona", "Mark"], 1, "General Knowledge", "Switzerland uses the Swiss Franc (CHF)."),
    ("Which Indian state has the highest population according to the official census?", ["Maharashtra", "Uttar Pradesh", "Bihar", "West Bengal"], 1, "Indian Geography", "Uttar Pradesh is India's most populous state with over 200 million residents."),
    ("What is the name of the nearest major spiral galaxy to the Milky Way?", ["Triangulum", "Andromeda (M31)", "Large Magellanic Cloud", "Centaurus A"], 1, "Science & Tech", "Andromeda is located roughly 2.5 million light-years away."),
    ("What does 'API' stand for in software engineering?", ["Application Programming Interface", "Advanced Program Instruction", "Automated Process Integrator", "App Protocol Infrastructure"], 0, "Science & Tech", "An API specifies how software components interact and exchange data."),
    ("Which gas makes up the majority of Venus's thick atmosphere?", ["Methane", "Carbon Dioxide", "Sulfuric acid gas", "Nitrogen"], 1, "Science & Tech", "Venus's atmosphere is over 96% CO₂, creating extreme runaway greenhouse heat."),
    ("Who was the founder of the Indian National Congress in 1885?", ["Dadabhai Naoroji", "Allan Octavian Hume", "W.C. Bonnerjee", "Dinshaw Wacha"], 1, "Indian History", "A.O. Hume, a retired British civil servant, catalyzed the founding of the INC in Bombay."),
    ("What is the SI unit of magnetic flux density?", ["Weber", "Tesla", "Henry", "Gauss"], 1, "Science & Tech", "The Tesla (T) is the SI unit for magnetic B-field intensity (1 T = 1 Wb/m²)."),
    ("Which gland in the human endocrine system is termed the 'Master Gland'?", ["Thyroid", "Pituitary Gland", "Adrenal Gland", "Hypothalamus"], 1, "Science & Tech", "The pituitary gland regulates multiple other hormone-secreting endocrine organs."),
    ("What is the term for a word, phrase, or sequence that reads the same backward as forward?", ["Anagram", "Palindrome", "Acronym", "Pangram"], 1, "General Knowledge", "A palindrome reads identical forwards and backwards (e.g., 'radar', 'madam')."),
    ("Which is the largest desert in the world by total surface area (including polar deserts)?", ["Sahara Desert", "Antarctic Polar Desert", "Arctic Desert", "Gobi Desert"], 1, "World Geography", "Antarctica is technically a desert due to low precipitation, spanning 14.2M km²."),
    ("Who was the first Indian citizen to win the prestigious Booker Prize in literature?", ["Arundhati Roy", "Salman Rushdie", "Kiran Desai", "Aravind Adiga"], 0, "General Knowledge", "Arundhati Roy won in 1997 for 'The God of Small Things'."),
    ("Which constitutional amendment reduced the voting age in India from 21 to 18 years?", ["42nd Amendment", "61st Amendment", "73rd Amendment", "86th Amendment"], 1, "Indian Polity", "The 61st Constitutional Amendment Act of 1988 enacted the lower voting age.")
]

for i, (q, opts, cidx, cat, exp) in enumerate(adults_hard_data, 1):
    add_q(adults_questions, f"a_hard_{i:03d}", q, opts, cidx, cat, "Hard", exp)

# Output files
with open("app/src/main/assets/questions_kids.json", "w", encoding="utf-8") as f:
    json.dump(kids_questions, f, indent=2, ensure_ascii=False)

with open("app/src/main/assets/questions_adults.json", "w", encoding="utf-8") as f:
    json.dump(adults_questions, f, indent=2, ensure_ascii=False)

# Also copy to flutter/assets for Flutter export completeness
with open("flutter/assets/questions_kids.json", "w", encoding="utf-8") as f:
    json.dump(kids_questions, f, indent=2, ensure_ascii=False)

with open("flutter/assets/questions_adults.json", "w", encoding="utf-8") as f:
    json.dump(adults_questions, f, indent=2, ensure_ascii=False)

print(f"Generated {len(kids_questions)} Kids questions and {len(adults_questions)} Adults questions successfully!")
