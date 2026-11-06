package com.aiplayground.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray

private val lessons = listOf(
    "What Is AI?" to "AI is software that finds patterns in information and uses them to generate answers, predictions, or content. It can be useful, but it can also make mistakes.",
    "Writing Better Prompts" to "A prompt is the instruction you give an AI. Be clear about your goal, include useful details, and say what format you want.",
    "Tips for Picture Editing" to "Describe what should change and what should stay the same. Mention the style, lighting, background, and important details. Check the result carefully.",
    "Getting Better Answers" to "If an answer is unclear, ask a smaller follow-up question. You can request examples, simpler wording, or a step-by-step explanation.",
    "Everyday AI" to "AI can help organize ideas, explain school topics, draft checklists, and plan projects. Review its suggestions before using them.",
    "Fact Checking" to "AI can sound confident while being wrong. Check important claims against reliable sources, especially dates, numbers, health, and safety information.",
    "AI Safety and Privacy" to "Avoid sharing passwords, private addresses, or sensitive personal details. Ask a trusted adult before using a new service or sharing information.",
    "Types of AI" to "Some AI works with text, some with images, audio, video, or recommendations. Different systems are designed for different tasks.",
    "Writing with AI" to "Use AI to brainstorm or improve a draft, then make sure the final work reflects your own understanding and follows your teacher's rules.",
    "Working with Information" to "Ask AI to group, summarize, or compare information. Check that the summary has not left out important context.",
    "Using Voice AI" to "Speak clearly, keep requests focused, and check the transcript if one is available. Voice tools may misunderstand names or noisy speech.",
    "Solving Problems with AI" to "Explain the problem, what you have tried, and what a good result looks like. For risky real-world tasks, get qualified help.",
    "Creative AI Ideas" to "Try using AI to invent characters, plan a game, create a theme, or explore different creative directions.",
    "Understanding AI Limitations" to "AI does not understand everything like a person. It may miss context, use outdated information, or invent details."
)

private data class PictureIdea(val title: String, val description: String, val example: String, val image: Int)

private val pictureIdeas = listOf(
    PictureIdea("Turn an Idea into Art", "Explore an original scene, art style, or wallpaper. AI can help turn a few words into a visual starting point.", "Create an original peaceful lake scene at sunset, with colourful reflections and a dreamy illustration style.", R.drawable.picture_ai_art),
    PictureIdea("Redesign a Room", "Try out decorating ideas and different room themes before changing anything in real life.", "Show a cosy game room with a pool table, comfortable seating, warm lighting, and a clean modern look.", R.drawable.picture_room_design),
    PictureIdea("Make a Poster", "Create a poster for a school event, club, hobby, or personal project. Check all wording carefully after generation.", "Design a bold, colourful poster for a community game night. Leave clear space for the date, time, and location.", R.drawable.picture_poster),
    PictureIdea("Create a Story World", "Visualize an original character, setting, or moment from a story you are writing.", "Illustrate a friendly young explorer discovering a hidden treehouse in a magical forest, in a colourful storybook style.", R.drawable.picture_story),
    PictureIdea("Learn with Visuals", "Turn a topic into a simple diagram or infographic that makes information easier to understand.", "Create a clear, colourful infographic showing the water cycle with simple labels and arrows for each stage.", R.drawable.picture_learning)
)

private val photoEditExamples = listOf(
    "Turn this photo into a playful cartoon scene while keeping the people recognizable.",
    "Give this photo a funny movie-poster look with a dramatic title and playful lighting.",
    "Change the background to a sunny beach while keeping the person and pose unchanged.",
    "Show how a smart-casual outfit with a jacket and sneakers could look on the person in this photo. Keep their face and pose unchanged.",
    "Add a harmless, funny detail to this photo, such as oversized sunglasses or a tiny parade of penguins in the background."
)

private val photoIdentifyExamples = listOf(
    "What is this household object? Describe the visible clues and what it is commonly used for.",
    "Can you identify this tool from the photo and explain its general purpose? Mention anything you are unsure about.",
    "Read the visible model or product label and explain what the information means.",
    "What kind of plant is shown? Point out the visible features and give a few possibilities if you are uncertain.",
    "Describe the main parts you can see in this machine or appliance photo and help me prepare questions for a qualified technician."
)

private val activities = listOf(
    "Create a Story", "Create an Image Prompt", "Brainstorm Ideas",
    "Discover Something New", "Plan a Project", "Play a Quiz", "Fix It"
)
private val challenges = listOf(
    "Ask AI to explain a topic you have always wondered about.",
    "Create a three-part plan for a small project.",
    "Ask AI for five unusual ideas for a story.",
    "Take an answer and ask AI to make it easier to understand.",
    "Ask AI to compare two everyday options fairly."
)

private val aiFacts = listOf(
    "AI systems learn patterns from examples; they do not automatically know whether an answer is true.",
    "Some AI tools can work with text, pictures, audio, and video—not just written questions.",
    "A more specific prompt often helps AI give a more useful answer.",
    "AI can make mistakes with dates, names, numbers, and details, so important facts should be checked.",
    "AI image tools can create new scenes or edit existing pictures from written instructions.",
    "AI chatbots generate responses based on patterns learned during training and the context you provide.",
    "You can often improve an AI answer by asking a follow-up question or requesting a different format.",
    "Some AI tools can summarize long documents, helping you find the main points more quickly.",
    "AI can help translate text between languages, but important translations may still need human review.",
    "AI does not have human experiences or feelings, even when its conversation sounds natural.",
    "Giving AI a clear goal, useful background, and any important limits can improve its response.",
    "AI can help brainstorm ideas, but people make the final decisions about what to use."
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PlaygroundApp() }
    }
}

@Composable
fun PlaygroundApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("playground", Context.MODE_PRIVATE) }
    var completedDay by remember { mutableStateOf(prefs.getString("daily", "") ?: "") }
    var page by remember { mutableStateOf("Home") }
    var showAiFact by remember { mutableStateOf(true) }
    val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CANADA).format(java.util.Date())
    val todaysChallenge = challenges[(today.hashCode().toUInt().toLong() % challenges.size).toInt()]
    val todaysAiFact = remember { aiFacts.random() }
    var selectedLesson by remember { mutableStateOf<Pair<String,String>?>(null) }
    var selectedPicture by remember { mutableStateOf<PictureIdea?>(null) }
    var activity by remember { mutableStateOf<String?>(null) }
    // These featured examples are chosen once when the app opens, then stay consistent during that session.
    val featuredPictureExample = remember { pictureIdeas.random() }
    val featuredPhotoEditExample = remember { photoEditExamples.random() }
    val featuredPhotoIdentifyExample = remember { photoIdentifyExamples.random() }
    fun copyText(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("AI Playground prompt", text))
    }

    MaterialTheme(colorScheme = lightColorScheme(
        primary = Color(0xFF6554E8), secondary = Color(0xFFFF8A4C),
        tertiary = Color(0xFF16B8A6), background = Color(0xFFFFF8F2), surface = Color.White
    )) {
        Scaffold(
            containerColor = Color(0xFFFFF8F2),
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                    listOf("Home", "Learn AI", "Daily Challenge", "Pictures").forEach { item ->
                        NavigationBarItem(
                            selected = page == item,
                            onClick = { page = item; selectedLesson = null; selectedPicture = null; activity = null },
                            icon = { Text(when(item) { "Home" -> "🏠"; "Learn AI" -> "📘"; "Daily Challenge" -> "⭐"; else -> "🖼️" }) },
                            label = { Text(item, fontSize = 9.sp, fontWeight = if (page == item) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF5B45D6), selectedTextColor = Color(0xFF5B45D6),
                                indicatorColor = Color(0xFFEAE5FF), unselectedIconColor = Color(0xFF8C879B),
                                unselectedTextColor = Color(0xFF8C879B)
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                if (selectedLesson != null) {
                    DetailScreen(selectedLesson!!.first, selectedLesson!!.second) { selectedLesson = null }
                } else if (selectedPicture != null) {
                    PictureDetailScreen(selectedPicture!!) { selectedPicture = null }
                } else if (activity != null) {
                    ActivityScreen(activity!!, onBack = { activity = null }, onTry = { text ->
                        copyText(text)
                    })
                } else when (page) {
                    "Learn AI" -> {
                        Header("Learn AI", "Short lessons to help you use AI wisely.")
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(lessons) { lesson ->
                                CardButton(lesson.first, "Read lesson") { selectedLesson = lesson }
                            }
                        }
                    }
                    "Pictures" -> {
                        Header("Pictures", "Explore, edit, and understand the world through photos.")
                        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item {
                                SectionTitle("🎨  Picture ideas")
                                ExamplePromptCard("A fresh idea every visit", featuredPictureExample.example) { copyText(featuredPictureExample.example) }
                            }
                            items(pictureIdeas) { idea -> PictureIdeaCard(idea) { selectedPicture = idea } }
                            item {
                                SectionTitle("📸  Fun photo makeovers")
                                Text("Try creative edits, playful effects, new backgrounds, or preview a different outfit style.", Modifier.padding(horizontal = 16.dp), color = Color(0xFF625B70), fontSize = 14.sp, lineHeight = 20.sp)
                                ExamplePromptCard("Random photo-editing example", featuredPhotoEditExample) { copyText(featuredPhotoEditExample) }
                            }
                            item {
                                SectionTitle("🔎  Identify things in photos")
                                Text("Show AI a picture of an object, tool, plant, label, or machine part and ask what it might be. Treat its answer as a starting point, not a guaranteed identification.", Modifier.padding(horizontal = 16.dp), color = Color(0xFF625B70), fontSize = 14.sp, lineHeight = 20.sp)
                                ExamplePromptCard("Random photo-identification example", featuredPhotoIdentifyExample) { copyText(featuredPhotoIdentifyExample) }
                            }
                        }
                    }
                    "Daily Challenge" -> {
                        Header("Daily Challenge", "A small activity to try today.")
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            val day = today
                            val challenge = todaysChallenge
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
                                Column(Modifier.padding(20.dp)) {
                                    Text("⭐ TODAY'S CHALLENGE", color = Color(0xFF5B4BDB), fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(12.dp))
                                    Text(challenge, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(16.dp))
                                    Button(onClick = {
                                        if (completedDay != day) {
                                            completedDay = day
                                            prefs.edit().putString("daily", day).apply()
                                        }
                                    }, enabled = completedDay != day, modifier = Modifier.fillMaxWidth()) {
                                        Text(if (completedDay == day) "Completed today ✓" else "Mark today’s challenge complete")
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(contentPadding = PaddingValues(bottom = 20.dp)) {
                            item {
                                if (showAiFact) {
                                    AiFactBanner(todaysAiFact) { showAiFact = false }
                                }
                            }
                            item {
                                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF684CE8), Color(0xFFB34DE0), Color(0xFFFF8C68)))).padding(24.dp)) {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text("🌈  YOUR CREATIVE SPACE", color = Color.White, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp, fontSize = 12.sp)
                                        Text("AI PLAYGROUND", color = Color.White, fontSize = 38.sp, lineHeight = 42.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                                        Text("What's the big deal with AI? Here's how to use it in your everyday life.", color = Color.White.copy(alpha = 0.98f), fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                            item { SectionTitle("🚀  Pick your adventure") }
                            items(activities) { name -> CardButton(name, "Tap to explore") { activity = name } }
                            item {
                                SectionTitle("⭐  Daily challenge")
                                CardButton("Daily Challenge", todaysChallenge) { page = "Daily Challenge" }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiFactBanner(fact: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F5FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("🧠  AI FACT OF THE DAY", color = Color(0xFF245B86), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                Text(fact, color = Color(0xFF29445A), fontSize = 14.sp, lineHeight = 20.sp)
            }
            TextButton(onClick = onDismiss, contentPadding = PaddingValues(6.dp)) {
                Text("✕", color = Color(0xFF45677F), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ExamplePromptCard(title: String, example: String, onCopy: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECE7FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = Color(0xFF4936A8), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(example, color = Color(0xFF332B4D), fontSize = 14.sp, lineHeight = 21.sp)
            Button(onClick = onCopy, modifier = Modifier.align(Alignment.End)) { Text("Copy example") }
        }
    }
}

@Composable
private fun PictureIdeaCard(idea: PictureIdea, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column {
            Image(painter = painterResource(idea.image), contentDescription = idea.title, modifier = Modifier.fillMaxWidth().height(155.dp), contentScale = ContentScale.Crop)
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(idea.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(idea.description, color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
                Text("Tap to see an example", color = Color(0xFF5B4BDB), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun PictureDetailScreen(idea: PictureIdea, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("🖼️  PICTURES", color = Color(0xFF6551E8), fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Image(painter = painterResource(idea.image), contentDescription = idea.title, modifier = Modifier.fillMaxWidth().height(210.dp), contentScale = ContentScale.Crop)
        Text(idea.title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text(idea.description, fontSize = 16.sp, lineHeight = 23.sp)
        Text("Example to try", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
            Text(idea.example, Modifier.padding(16.dp), fontSize = 15.sp, lineHeight = 22.sp)
        }
        Text("Tip: Add your own details, then review the result. AI images can contain mistakes.", color = Color.Gray, fontSize = 13.sp)
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back to Pictures") }
    }
}

@Composable
fun Header(title: String, subtitle: String) {
    Column(
        Modifier.fillMaxWidth()
            .background(Brush.linearGradient(listOf(Color(0xFF6551E8), Color(0xFFB24DDD), Color(0xFFFF9168))))
            .padding(horizontal = 22.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("✨  AI PLAYGROUND", color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
        Text(title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, color = Color.White.copy(alpha = 0.96f), fontSize = 14.sp)
    }
}
@Composable
fun SectionTitle(title: String) {
    Text(title, Modifier.padding(start = 18.dp, top = 20.dp, bottom = 8.dp), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF33265F))
}
@Composable
fun CardButton(title: String, subtitle: String, onClick: () -> Unit) {
    val palettes = listOf(
        Color(0xFFFFE7D8), Color(0xFFE9E3FF), Color(0xFFDDF7F0),
        Color(0xFFFFF1C9), Color(0xFFE1F0FF), Color(0xFFFFE2EF), Color(0xFFE9F3D8)
    )
    val icons = mapOf(
        "Create a Story" to "📚", "Create an Image Prompt" to "🎨", "Brainstorm Ideas" to "💡",
        "Discover Something New" to "🔎", "Plan a Project" to "🗺️", "Play a Quiz" to "🧩", "Fix It" to "🛠️",
        "Daily Challenge" to "⭐", "Read lesson" to "📖"
    )
    val tint = palettes[(title.hashCode().toUInt().toLong() % palettes.size).toInt()]
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = tint), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 17.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(color = Color.White.copy(alpha = 0.82f), shape = RoundedCornerShape(17.dp)) {
                Text(icons[title] ?: "🌈", modifier = Modifier.padding(11.dp), fontSize = 25.sp)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color(0xFF30264F))
                Text(subtitle, color = Color(0xFF625B70), fontSize = 13.sp, lineHeight = 18.sp)
            }
            Text("›", fontSize = 30.sp, fontWeight = FontWeight.Light, color = Color(0xFF6652C8))
        }
    }
}
@Composable
fun DetailScreen(title: String, body: String, isPrompt: Boolean = false, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("← Back", color = Color(0xFF6551E8), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text(body, fontSize = 17.sp, lineHeight = 25.sp)
        if (isPrompt) {
            Button(onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("AI Playground prompt", body))
            }) { Text("Copy prompt") }
        }
        OutlinedButton(onClick = onBack) { Text("Back to AI Playground") }
    }
}
@Composable
fun ActivityScreen(name: String, onBack: () -> Unit, onTry: (String) -> Unit) {
    var input by remember { mutableStateOf("") }
    val templates = mapOf(
        "Create a Story" to "Help me create an original story about: [my idea]. Ask me two questions first.",
        "Create an Image Prompt" to "Write a detailed, original image-generation prompt for this idea: [my idea]. Include setting, lighting, mood, and style.",
        "Brainstorm Ideas" to "Give me 10 creative ideas for: [my topic]. Make the ideas varied.",
        "Discover Something New" to "Teach me something surprising about: [topic] in simple language.",
        "Plan a Project" to "Help me make a safe, realistic plan for this project: [project]. Break it into manageable steps.",
        "Play a Quiz" to "Make a 5-question quiz about [topic]. Ask one question at a time and wait for my answer.",
        "Fix It" to "Help me document a maintenance roadblock professionally. I can provide a photo of the affected part, equipment, machine, or site condition. Describe the visible issue, summarize the potential impact on the work, and help draft a clear maintenance report or work-order note. Do not guess at a safety-critical diagnosis or recommend operating or repairing equipment; identify when a qualified technician or supervisor should assess it."
    )
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("← Back", color = Color(0xFF6551E8), fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
        Text(name, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        if (name == "Fix It") {
            Text(
                "You can take a clear photo of the part, machine, equipment, or other maintenance roadblock and attach it to your AI chat with this prompt. AI can help describe what is visible and prepare a professional maintenance note. Have a qualified person assess safety-critical issues.",
                fontSize = 14.sp, lineHeight = 21.sp, color = Color(0xFF625B70)
            )
        } else {
            Text("Add a few details, then copy your prompt into your AI chat.")
        }
        OutlinedTextField(input, { input = it }, label = { Text(if (name == "Fix It") "Describe the maintenance issue" else "Your idea or topic") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Button(onClick = { onTry(templates[name]!!.replace(Regex("\\\\[[^]]+\\\\]"), input.ifBlank { "my idea" })) }, modifier = Modifier.fillMaxWidth()) {
            Text("Copy my prompt")
        }
        OutlinedButton(onClick = onBack) { Text("Back") }
    }
}
