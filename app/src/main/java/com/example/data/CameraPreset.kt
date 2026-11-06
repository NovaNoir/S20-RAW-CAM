package com.example.data

enum class PresetCategory(val title: String) {
    ALL("All (1,008)"),
    DAYLIGHT("Daylight"),
    PORTRAIT("Portrait"),
    NIGHT_ASTRO("Night & Astro"),
    AUTOMOTIVE("Automotive"),
    ARCHITECTURE("Architecture"),
    LANDSCAPE("Landscape"),
    INDOOR("Indoor"),
    CREATIVE("Creative")
}

data class CameraPreset(
    val id: Int,
    val familyId: Int = 1,
    val familyName: String = "",
    val name: String,
    val category: PresetCategory,
    val lens: String,
    val iso: String,
    val isoNum: Int,
    val shutter: String,
    val shutterSeconds: Float,
    val wb: String,
    val wbKelvin: Int,
    val focus: String,
    val ev: String,
    val evValue: Float,
    val bestUse: String,
    val tips: String,
    val meter: String = "Matrix",
    val variant: String = "Normal",
    val variantCode: String = "N",
    val isFavorite: Boolean = false,
    val userNote: String = ""
) {
    val displayName: String
        get() = "$name ($variantCode · $variant)"
}

data class SceneFamilyInfo(
    val id: Int,
    val name: String,
    val category: PresetCategory,
    val bestUse: String,
    val baseTip: String,
    val presetCount: Int = 18
)

internal data class ExposureBase(
    val lens: String,
    val iso: Int,
    val shutter: String,
    val wbKelvin: Int
)

internal data class SceneFamily(
    val name: String,
    val category: PresetCategory,
    val bestUse: String,
    val baseTip: String,
    val bases: List<ExposureBase>
)

internal data class ControlVariant(
    val code: String,
    val name: String,
    val ev: String,
    val evValue: Float,
    val meter: String,
    val focus: String,
    val tipSuffix: String
)

private val controlVariants = listOf(
    ControlVariant("N", "Normal", "0", 0.0f, "Matrix", "AF", "Normal baseline exposure with multi-zone matrix metering and autofocus."),
    ControlVariant("HL", "Highlight protection", "-0.3", -0.3f, "Matrix", "AF", "-0.3 EV exposure bias preserves specular highlights and cloud detail from clipping in RAW."),
    ControlVariant("DR", "Dynamic-range bias", "-0.7", -0.7f, "Matrix", "AF", "-0.7 EV bias maximizes highlight headroom for extreme high-contrast post-production in Lightroom."),
    ControlVariant("SP", "Subject metering", "0", 0.0f, "Spot", "AF", "Spot metering pins exposure strictly to the primary subject regardless of background brightness."),
    ControlVariant("CT", "Center-weighted", "-0.3", -0.3f, "Center", "AF", "Center-weighted metering with -0.3 EV bias balances centered subject tones with ambient backdrop."),
    ControlVariant("MF", "Manual-focus", "0", 0.0f, "Matrix", "MF", "Manual focus prevents AF hunting on low-contrast, distant, or fast-moving elements.")
)

private fun formatShutter(raw: String): String {
    val clean = raw.trim().removeSuffix("s").trim()
    return "$clean s"
}

private fun parseShutterSeconds(raw: String): Float {
    return try {
        val clean = raw.trim().removeSuffix("s").trim()
        if (clean.contains("/")) {
            val parts = clean.split("/")
            val num = parts[0].toFloatOrNull() ?: 1f
            val den = parts[1].toFloatOrNull() ?: 1f
            num / den
        } else {
            clean.toFloatOrNull() ?: 0.002f
        }
    } catch (e: Exception) {
        0.002f
    }
}

private val sceneFamilies = listOf(
    // 1. Everyday Daylight (1..18)
    SceneFamily(
        "Everyday Daylight", PresetCategory.DAYLIGHT,
        "General daylight; clean detail and low noise.",
        "The base starting point for standard outdoor illumination. Base ISO 50 delivers the cleanest signal from the 108MP sensor.",
        listOf(
            ExposureBase("1x", 50, "1/500", 5200),
            ExposureBase("1x", 50, "1/1000", 5600),
            ExposureBase("1x", 100, "1/500", 6000)
        )
    ),
    // 2. Bright Midday (19..36)
    SceneFamily(
        "Bright Midday", PresetCategory.DAYLIGHT,
        "Harsh high-noon sun and direct overhead lighting.",
        "Fast shutter speeds prevent clipping blown highlights in bright direct sunlight.",
        listOf(
            ExposureBase("1x", 50, "1/1000", 5500),
            ExposureBase("1x", 50, "1/2000", 5600),
            ExposureBase("1x", 100, "1/1000", 6000)
        )
    ),
    // 3. Open Shade (37..54)
    SceneFamily(
        "Open Shade", PresetCategory.DAYLIGHT,
        "Subjects out of direct sunlight, in building or tree shadow.",
        "Higher Kelvin (6500–7000K) warms up cool sky ambient bounce in open shade.",
        listOf(
            ExposureBase("1x", 50, "1/250", 6500),
            ExposureBase("1x", 100, "1/250", 6500),
            ExposureBase("1x", 200, "1/250", 7000)
        )
    ),
    // 4. Overcast (55..72)
    SceneFamily(
        "Overcast", PresetCategory.DAYLIGHT,
        "Soft diffused light through flat, uniform cloud cover.",
        "Even lighting allows clean color capture; 6000K counters overcast blue cast.",
        listOf(
            ExposureBase("1x", 50, "1/250", 6000),
            ExposureBase("1x", 100, "1/500", 6200),
            ExposureBase("1x", 100, "1/250", 6500)
        )
    ),
    // 5. Cloudy Warm (73..90)
    SceneFamily(
        "Cloudy Warm", PresetCategory.DAYLIGHT,
        "Cloudy skies with warm sunset or golden breaks.",
        "Warmer 6200–6800K balances golden highlights through thick clouds.",
        listOf(
            ExposureBase("1x", 50, "1/250", 6500),
            ExposureBase("1x", 100, "1/250", 6800),
            ExposureBase("1x", 100, "1/500", 6200)
        )
    ),
    // 6. Golden Hour Front (91..108)
    SceneFamily(
        "Golden Hour Front", PresetCategory.DAYLIGHT,
        "Warm low-angle sun illuminating the front of subjects.",
        "6000–6500K accentuates natural golden glow on skin and building facades.",
        listOf(
            ExposureBase("1x", 50, "1/250", 6000),
            ExposureBase("1x", 50, "1/500", 6200),
            ExposureBase("1x", 100, "1/250", 6500)
        )
    ),
    // 7. Golden Hour Backlit (109..126)
    SceneFamily(
        "Golden Hour Backlit", PresetCategory.DAYLIGHT,
        "Shooting directly towards low evening sun with subject in front.",
        "Negative EV prevents clipping rim lights while preserving rich RAW flare.",
        listOf(
            ExposureBase("1x", 50, "1/500", 6000),
            ExposureBase("1x", 100, "1/500", 6500),
            ExposureBase("1x", 100, "1/250", 6200)
        )
    ),
    // 8. Sunset Sky (127..144)
    SceneFamily(
        "Sunset Sky", PresetCategory.DAYLIGHT,
        "Vibrant twilight gradients, colorful horizon clouds.",
        "Expose for the bright horizon; use highlight protection to preserve color saturation.",
        listOf(
            ExposureBase("1x", 50, "1/500", 6500),
            ExposureBase("1x", 50, "1/1000", 7000),
            ExposureBase("1x", 100, "1/500", 6800)
        )
    ),
    // 9. Blue Hour (145..162)
    SceneFamily(
        "Blue Hour", PresetCategory.DAYLIGHT,
        "Dusk after sunset before complete darkness; deep blue ambient sky.",
        "4800–5200K renders natural cool twilight atmosphere against warm city streetlights.",
        listOf(
            ExposureBase("1x", 50, "1/60", 5000),
            ExposureBase("1x", 100, "1/60", 5200),
            ExposureBase("1x", 200, "1/125", 4800)
        )
    ),
    // 10. Street Day (163..180)
    SceneFamily(
        "Street Day", PresetCategory.DAYLIGHT,
        "Candid urban walking, pedestrians, street scenes in daylight.",
        "1/500s or 1/1000s shutter speed freezes walking pedestrians without motion blur.",
        listOf(
            ExposureBase("1x", 50, "1/500", 5500),
            ExposureBase("1x", 100, "1/500", 5600),
            ExposureBase("1x", 100, "1/1000", 5800)
        )
    ),
    // 11. Street Shadows (181..198)
    SceneFamily(
        "Street Shadows", PresetCategory.DAYLIGHT,
        "High-contrast urban streets with strong architectural shadows.",
        "Dynamic-range bias (-0.7 EV) keeps bright pavement from blowing out.",
        listOf(
            ExposureBase("1x", 50, "1/500", 5200),
            ExposureBase("1x", 50, "1/1000", 5400),
            ExposureBase("1x", 100, "1/500", 5600)
        )
    ),
    // 12. Street Architecture (199..216)
    SceneFamily(
        "Street Architecture", PresetCategory.ARCHITECTURE,
        "City facades, street corners, building geometric perspectives.",
        "Matrix metering balances sky and shadow details across geometric facades.",
        listOf(
            ExposureBase("1x", 50, "1/250", 5200),
            ExposureBase("1x", 50, "1/500", 5500),
            ExposureBase("1x", 100, "1/250", 5600)
        )
    ),
    // 13. Architecture Ultra-Wide (217..234)
    SceneFamily(
        "Architecture Ultra-Wide", PresetCategory.ARCHITECTURE,
        "Expansive skyscrapers, building interiors, dramatic perspective with 0.5x.",
        "Keep the phone perpendicular to reduce keystone converging line distortion.",
        listOf(
            ExposureBase("0.5x", 50, "1/250", 5200),
            ExposureBase("0.5x", 50, "1/500", 5500),
            ExposureBase("0.5x", 100, "1/250", 6000)
        )
    ),
    // 14. Architecture Tele (235..252)
    SceneFamily(
        "Architecture Tele", PresetCategory.ARCHITECTURE,
        "Compressed architectural facades, high-rise roofs, decorative detail with 4x periscope.",
        "Telephoto lens isolates repeating patterns and window grids with zero perspective bend.",
        listOf(
            ExposureBase("4x", 50, "1/500", 5200),
            ExposureBase("4x", 100, "1/500", 5500),
            ExposureBase("4x", 100, "1/1000", 5600)
        )
    ),
    // 15. Landscape Neutral (253..270)
    SceneFamily(
        "Landscape Neutral", PresetCategory.LANDSCAPE,
        "Expansive vistas, open fields, mountains under natural sunlight.",
        "Base ISO 50 ensures maximum resolution and color fidelity for wide landscapes.",
        listOf(
            ExposureBase("1x", 50, "1/250", 5600),
            ExposureBase("1x", 50, "1/500", 5800),
            ExposureBase("1x", 100, "1/250", 6000)
        )
    ),
    // 16. Landscape Dramatic (271..288)
    SceneFamily(
        "Landscape Dramatic", PresetCategory.LANDSCAPE,
        "Stormy skies, jagged peaks, high-contrast atmospheric landscapes.",
        "Underexpose slightly to enhance cloud texture and mood in landscape RAW files.",
        listOf(
            ExposureBase("1x", 50, "1/500", 6000),
            ExposureBase("1x", 50, "1/1000", 6200),
            ExposureBase("1x", 100, "1/500", 6500)
        )
    ),
    // 17. Foliage (289..306)
    SceneFamily(
        "Foliage", PresetCategory.LANDSCAPE,
        "Forests, trees, garden flora, lush green vegetation.",
        "Slightly warmer Kelvin prevents greens from appearing sickly or overly digital.",
        listOf(
            ExposureBase("1x", 50, "1/250", 5600),
            ExposureBase("1x", 100, "1/250", 5800),
            ExposureBase("1x", 50, "1/500", 6000)
        )
    ),
    // 18. Beach / Sand (307..324)
    SceneFamily(
        "Beach / Sand", PresetCategory.LANDSCAPE,
        "Bright sand dunes, coastal shorelines, intense tropical glare.",
        "High ambient reflection demands fast shutter speeds (1/1000–1/2000s) to avoid clipping.",
        listOf(
            ExposureBase("1x", 50, "1/1000", 5600),
            ExposureBase("1x", 50, "1/2000", 5800),
            ExposureBase("1x", 100, "1/1000", 6000)
        )
    ),
    // 19. Water Reflection (325..342)
    SceneFamily(
        "Water Reflection", PresetCategory.LANDSCAPE,
        "Lake mirrors, wet coastal shallows, reflective city puddles.",
        "Negative EV maintains saturated specular reflection on water surfaces.",
        listOf(
            ExposureBase("1x", 50, "1/500", 5600),
            ExposureBase("1x", 50, "1/1000", 5800),
            ExposureBase("1x", 100, "1/500", 6000)
        )
    ),
    // 20. Portrait Daylight (343..360)
    SceneFamily(
        "Portrait Daylight", PresetCategory.PORTRAIT,
        "Outdoor portraits with natural background compression using 4x tele.",
        "4x optical telephoto flatters facial proportions with natural optical falloff.",
        listOf(
            ExposureBase("4x", 50, "1/250", 5200),
            ExposureBase("4x", 100, "1/250", 5400),
            ExposureBase("4x", 100, "1/500", 5600)
        )
    ),
    // 21. Portrait Golden Hour (361..378)
    SceneFamily(
        "Portrait Golden Hour", PresetCategory.PORTRAIT,
        "Flattering warm late-afternoon portraits with rich skin tones.",
        "6000–6500K warms skin and hair highlights for a cinematic glow.",
        listOf(
            ExposureBase("4x", 50, "1/250", 6000),
            ExposureBase("4x", 50, "1/500", 6200),
            ExposureBase("4x", 100, "1/250", 6500)
        )
    ),
    // 22. Portrait Shade (379..396)
    SceneFamily(
        "Portrait Shade", PresetCategory.PORTRAIT,
        "Soft, even light portraits in open shade without harsh squints.",
        "Warm white balance (6500K+) neutralizes cool ambient shade reflections on skin.",
        listOf(
            ExposureBase("4x", 100, "1/250", 6500),
            ExposureBase("4x", 200, "1/250", 6800),
            ExposureBase("4x", 100, "1/500", 6200)
        )
    ),
    // 23. Portrait Backlit (397..414)
    SceneFamily(
        "Portrait Backlit", PresetCategory.PORTRAIT,
        "Subjects positioned against the sun for hair rim light and halo glow.",
        "Use Spot metering on the subject's face to prevent silhouette underexposure.",
        listOf(
            ExposureBase("4x", 100, "1/500", 6000),
            ExposureBase("4x", 100, "1/250", 6200),
            ExposureBase("4x", 200, "1/500", 6500)
        )
    ),
    // 24. Portrait Environmental (415..432)
    SceneFamily(
        "Portrait Environmental", PresetCategory.PORTRAIT,
        "Wider portraits showing the person within their workplace, home, or scenery.",
        "The 1x main camera captures crisp context while maintaining sharp subject focus.",
        listOf(
            ExposureBase("1x", 50, "1/250", 5200),
            ExposureBase("1x", 100, "1/250", 5600),
            ExposureBase("1x", 100, "1/500", 5800)
        )
    ),
    // 25. Fashion Full Body (433..450)
    SceneFamily(
        "Fashion Full Body", PresetCategory.PORTRAIT,
        "Full-length outfit and style photography with sharp clothing texture.",
        "1/500s prevents micro-motion blur on fabric movement and walking poses.",
        listOf(
            ExposureBase("1x", 50, "1/500", 5400),
            ExposureBase("1x", 100, "1/500", 5600),
            ExposureBase("1x", 50, "1/1000", 5800)
        )
    ),
    // 26. Face Detail (451..468)
    SceneFamily(
        "Face Detail", PresetCategory.PORTRAIT,
        "Close-up headshots, eyes, makeup, jewelry detail with 4x tele.",
        "Maintain safe minimum focus distance on the 4x periscope lens for tack-sharp eyes.",
        listOf(
            ExposureBase("4x", 50, "1/250", 5200),
            ExposureBase("4x", 100, "1/250", 5400),
            ExposureBase("4x", 100, "1/500", 5600)
        )
    ),
    // 27. Car Hero Day (469..486)
    SceneFamily(
        "Car Hero Day", PresetCategory.AUTOMOTIVE,
        "Static 3/4 car beauty shots in daytime parking or open locations.",
        "Base ISO 50 delivers immaculate bodywork paint gloss and clean reflections.",
        listOf(
            ExposureBase("1x", 50, "1/500", 5600),
            ExposureBase("1x", 50, "1/1000", 5800),
            ExposureBase("1x", 100, "1/500", 6000)
        )
    ),
    // 28. Car Paint Detail (487..504)
    SceneFamily(
        "Car Paint Detail", PresetCategory.AUTOMOTIVE,
        "Close-up metallic flakes, emblems, body creases, curves with 4x tele.",
        "Highlight protection (-0.3 EV) keeps metallic paint specular sparks crisp.",
        listOf(
            ExposureBase("4x", 50, "1/500", 5500),
            ExposureBase("4x", 100, "1/500", 5600),
            ExposureBase("4x", 50, "1/1000", 5800)
        )
    ),
    // 29. Car Wheels Brakes (505..522)
    SceneFamily(
        "Car Wheels Brakes", PresetCategory.AUTOMOTIVE,
        "Alloy rims, caliper badges, tire tread details.",
        "Fast shutter and negative EV preserve dark rim spoke and rotor textures.",
        listOf(
            ExposureBase("4x", 50, "1/500", 5200),
            ExposureBase("4x", 100, "1/500", 5400),
            ExposureBase("4x", 100, "1/1000", 5600)
        )
    ),
    // 30. Car Interior Day (523..540)
    SceneFamily(
        "Car Interior Day", PresetCategory.AUTOMOTIVE,
        "Dashboard, steering wheel, upholstery, cockpit detailing.",
        "Interior lighting is dimmer than outdoors; ISO 200–400 with 1/60s balances cabin light.",
        listOf(
            ExposureBase("1x", 200, "1/60", 4500),
            ExposureBase("1x", 400, "1/125", 4300),
            ExposureBase("1x", 200, "1/125", 4800)
        )
    ),
    // 31. Car Rolling Freeze (541..558)
    SceneFamily(
        "Car Rolling Freeze", PresetCategory.AUTOMOTIVE,
        "Car-to-car rolling tracking shots freezing wheels and chassis motion.",
        "Fast 1/1000s+ shutter speed freezes road vibration and vehicle motion crisp.",
        listOf(
            ExposureBase("1x", 100, "1/1000", 5500),
            ExposureBase("1x", 200, "1/1000", 5600),
            ExposureBase("1x", 200, "1/2000", 5800)
        )
    ),
    // 32. Car Rolling Panning (559..576)
    SceneFamily(
        "Car Rolling Panning", PresetCategory.AUTOMOTIVE,
        "Dynamic motion blur background and spinning wheels with sharp car body.",
        "Smoothly track the moving vehicle at 1/30–1/60s to blur background speed lines.",
        listOf(
            ExposureBase("1x", 50, "1/30", 5600),
            ExposureBase("1x", 50, "1/60", 5600),
            ExposureBase("1x", 100, "1/60", 5800)
        )
    ),
    // 33. Car Sunset (577..594)
    SceneFamily(
        "Car Sunset", PresetCategory.AUTOMOTIVE,
        "Dramatic evening automotive photography with horizon reflections on bodywork.",
        "Underexpose to preserve deep orange sunset reflections along body contours.",
        listOf(
            ExposureBase("1x", 50, "1/500", 6500),
            ExposureBase("1x", 100, "1/500", 6800),
            ExposureBase("1x", 50, "1/1000", 7000)
        )
    ),
    // 34. Car Night (595..612)
    SceneFamily(
        "Car Night", PresetCategory.AUTOMOTIVE,
        "Night automotive shots under streetlamps, gas stations, or city neon.",
        "Lower Kelvin (3500–3800K) complements urban night lighting and headlights.",
        listOf(
            ExposureBase("1x", 200, "1/30", 3800),
            ExposureBase("1x", 400, "1/60", 3600),
            ExposureBase("1x", 800, "1/60", 3500)
        )
    ),
    // 35. Car Showroom (613..630)
    SceneFamily(
        "Car Showroom", PresetCategory.AUTOMOTIVE,
        "Indoor convention centers, dealership floors, artificial overhead lights.",
        "4000K neutralizes mixed indoor lighting and spotlights on showroom clearcoats.",
        listOf(
            ExposureBase("1x", 200, "1/60", 4000),
            ExposureBase("1x", 400, "1/125", 4200),
            ExposureBase("1x", 400, "1/60", 3800)
        )
    ),
    // 36. Food Daylight (631..648)
    SceneFamily(
        "Food Daylight", PresetCategory.INDOOR,
        "Natural window-lit culinary dishes, cafe plates, fresh ingredients.",
        "5200–5600K preserves accurate, appetizing colors of sauces and fresh produce.",
        listOf(
            ExposureBase("1x", 50, "1/125", 5200),
            ExposureBase("1x", 100, "1/125", 5600),
            ExposureBase("1x", 100, "1/250", 5800)
        )
    ),
    // 37. Food Warm Indoor (649..666)
    SceneFamily(
        "Food Warm Indoor", PresetCategory.INDOOR,
        "Restaurant dining tables under warm incandescent overhead bulbs.",
        "3000–3200K WB tames heavy orange restaurant casts without losing dinner warmth.",
        listOf(
            ExposureBase("1x", 200, "1/60", 3200),
            ExposureBase("1x", 400, "1/60", 3000),
            ExposureBase("1x", 400, "1/125", 3400)
        )
    ),
    // 38. Cafe Window (667..684)
    SceneFamily(
        "Cafe Window", PresetCategory.INDOOR,
        "Coffee cups, brunch setups with directional window side lighting.",
        "Side lighting carves depth and steam; Center metering balances cup with table.",
        listOf(
            ExposureBase("1x", 100, "1/125", 4800),
            ExposureBase("1x", 200, "1/125", 5000),
            ExposureBase("1x", 200, "1/60", 4500)
        )
    ),
    // 39. Restaurant Ambient (685..702)
    SceneFamily(
        "Restaurant Ambient", PresetCategory.INDOOR,
        "Low-light romantic dinner atmosphere, candlelit cuisine.",
        "ISO 400–800 at 1/60s captures intimate candlelit ambiance without camera shake.",
        listOf(
            ExposureBase("1x", 400, "1/60", 3200),
            ExposureBase("1x", 800, "1/60", 3000),
            ExposureBase("1x", 400, "1/125", 3400)
        )
    ),
    // 40. Indoor Portrait (703..720)
    SceneFamily(
        "Indoor Portrait", PresetCategory.INDOOR,
        "Portraits inside homes, offices, studios with artificial light.",
        "Keep shutter at 1/125s to stop blink and head motion in low indoor light.",
        listOf(
            ExposureBase("1x", 400, "1/125", 4000),
            ExposureBase("1x", 800, "1/125", 3800),
            ExposureBase("1x", 400, "1/60", 4200)
        )
    ),
    // 41. Window Portrait (721..738)
    SceneFamily(
        "Window Portrait", PresetCategory.INDOOR,
        "Side-lit indoor portrait with subject turned toward a bright window.",
        "Spot meter the lit side of the face to prevent blown highlights.",
        listOf(
            ExposureBase("1x", 200, "1/125", 4800),
            ExposureBase("1x", 400, "1/125", 5000),
            ExposureBase("1x", 200, "1/250", 5200)
        )
    ),
    // 42. Night Street (739..756)
    SceneFamily(
        "Night Street", PresetCategory.NIGHT_ASTRO,
        "Handheld urban night photography, illuminated storefronts, pedestrians.",
        "1/30–1/60s relies on OIS; ISO 200–400 provides clean low-noise street night shots.",
        listOf(
            ExposureBase("1x", 200, "1/30", 3800),
            ExposureBase("1x", 400, "1/30", 3600),
            ExposureBase("1x", 800, "1/60", 3500)
        )
    ),
    // 43. Night City Tripod (757..774)
    SceneFamily(
        "Night City Tripod", PresetCategory.NIGHT_ASTRO,
        "Skylines, illuminated bridges, static city architecture on tripod.",
        "Long exposure (2–4s) at ISO 50 produces grain-free RAW night cityscapes.",
        listOf(
            ExposureBase("1x", 50, "2s", 4000),
            ExposureBase("1x", 50, "4s", 3800),
            ExposureBase("1x", 100, "2s", 3600)
        )
    ),
    // 44. Neon (775..792)
    SceneFamily(
        "Neon", PresetCategory.NIGHT_ASTRO,
        "Glowing neon signs, night market glow, vibrant luminescent tubes.",
        "Expose for the glowing neon tubes to keep electric colors intensely saturated.",
        listOf(
            ExposureBase("1x", 100, "1/60", 3500),
            ExposureBase("1x", 200, "1/60", 3300),
            ExposureBase("1x", 100, "1/125", 3600)
        )
    ),
    // 45. Night Architecture (793..810)
    SceneFamily(
        "Night Architecture", PresetCategory.NIGHT_ASTRO,
        "Floodlit monuments, lit historic facades, illuminated buildings on tripod.",
        "ISO 50 with 2–4s exposure gives pure dynamic range on illuminated stonework.",
        listOf(
            ExposureBase("1x", 50, "2s", 3800),
            ExposureBase("1x", 50, "4s", 4000),
            ExposureBase("1x", 100, "2s", 3600)
        )
    ),
    // 46. Traffic Trails (811..828)
    SceneFamily(
        "Traffic Trails", PresetCategory.NIGHT_ASTRO,
        "Headlight and taillight light streaks on busy highways or intersections.",
        "Tripod required: 5–10s exposure weaves red taillights and white headlights into ribbons.",
        listOf(
            ExposureBase("1x", 50, "5s", 4000),
            ExposureBase("1x", 50, "10s", 3800),
            ExposureBase("1x", 100, "5s", 3600)
        )
    ),
    // 47. Waterfall Smooth (829..846)
    SceneFamily(
        "Waterfall Smooth", PresetCategory.LANDSCAPE,
        "Silky, misty cascades and flowing forest rivers on tripod.",
        "1–2s exposure creates ethereal misty water flow while keeping rocks needle-sharp.",
        listOf(
            ExposureBase("1x", 50, "1s", 5500),
            ExposureBase("1x", 50, "2s", 5600),
            ExposureBase("1x", 100, "1s", 6000)
        )
    ),
    // 48. Water Motion (847..864)
    SceneFamily(
        "Water Motion", PresetCategory.LANDSCAPE,
        "Ocean shore waves, churning rapids with retained water texture.",
        "1/4s shutter retains dynamic splash texture and crests without turning into total fog.",
        listOf(
            ExposureBase("1x", 50, "1/4", 5500),
            ExposureBase("1x", 50, "1/2", 5600),
            ExposureBase("1x", 100, "1/4", 6000)
        )
    ),
    // 49. Light Painting (865..882)
    SceneFamily(
        "Light Painting", PresetCategory.CREATIVE,
        "Drawing with flashlights, sparklers, or LED tubes in darkness.",
        "Tripod required: manual focus on subject; move light source during 5–10s exposure.",
        listOf(
            ExposureBase("1x", 50, "5s", 4000),
            ExposureBase("1x", 50, "10s", 3800),
            ExposureBase("1x", 100, "5s", 3600)
        )
    ),
    // 50. Silhouette (883..900)
    SceneFamily(
        "Silhouette", PresetCategory.CREATIVE,
        "Dramatic black subject profile against bright sky, sunset, or backlight.",
        "Meter strictly for the bright sky; deep negative EV renders foreground in silhouette.",
        listOf(
            ExposureBase("1x", 50, "1/500", 6000),
            ExposureBase("1x", 50, "1/1000", 6500),
            ExposureBase("1x", 100, "1/500", 6800)
        )
    ),
    // 51. Moon (901..918)
    SceneFamily(
        "Moon", PresetCategory.NIGHT_ASTRO,
        "Lunar craters, full moon and crescent moon photography with 4x tele.",
        "The moon is in direct sunlight in space! Use low ISO 50 and fast 1/250s shutter to resolve craters.",
        listOf(
            ExposureBase("4x", 50, "1/250", 5000),
            ExposureBase("4x", 50, "1/500", 5000),
            ExposureBase("4x", 100, "1/500", 4800)
        )
    ),
    // 52. Stars Manual (919..936)
    SceneFamily(
        "Stars Manual", PresetCategory.NIGHT_ASTRO,
        "Night sky star fields, basic celestial photography on tripod.",
        "Tripod required: set manual focus to infinity. 15–20s exposure captures pinpoint stars before Earth rotation causes star trailing.",
        listOf(
            ExposureBase("1x", 800, "15s", 3800),
            ExposureBase("1x", 800, "20s", 3800),
            ExposureBase("1x", 1600, "20s", 3600)
        )
    ),
    // 53. Milky Way Attempt (937..954)
    SceneFamily(
        "Milky Way Attempt", PresetCategory.NIGHT_ASTRO,
        "Galactic core photography in dark sky zones (Bortle 1–3) on tripod.",
        "Tripod required: 15–20s at ISO 1600–3200; cool Kelvin (3500–3700K) enhances cosmic contrast.",
        listOf(
            ExposureBase("1x", 1600, "15s", 3600),
            ExposureBase("1x", 1600, "20s", 3700),
            ExposureBase("1x", 3200, "15s", 3500)
        )
    ),
    // 54. Low-Light Landscape (955..972)
    SceneFamily(
        "Low-Light Landscape", PresetCategory.NIGHT_ASTRO,
        "Landscapes at dusk, moonlight, or deep pre-dawn twilight.",
        "1/15–1/30s handheld with steady grip or short tripod exposure for clean horizon tones.",
        listOf(
            ExposureBase("1x", 200, "1/15", 4000),
            ExposureBase("1x", 400, "1/30", 3800),
            ExposureBase("1x", 800, "1/60", 3600)
        )
    ),
    // 55. B&W Contrast (973..990)
    SceneFamily(
        "B&W Contrast", PresetCategory.CREATIVE,
        "High-contrast architectural shadows, graphic street shapes for black & white conversion.",
        "Expert RAW preserves deep shadow gradients and bright highlight rolloff for B&W grading.",
        listOf(
            ExposureBase("1x", 50, "1/250", 5200),
            ExposureBase("1x", 100, "1/500", 5400),
            ExposureBase("1x", 50, "1/500", 5600)
        )
    ),
    // 56. Neutral RAW Master (991..1008)
    SceneFamily(
        "Neutral RAW Master", PresetCategory.CREATIVE,
        "Color-accurate uncompressed RAW capture for multi-light studio or precise editing.",
        "Unprocessed linear baseline ideal for color grading in Adobe Lightroom Mobile.",
        listOf(
            ExposureBase("1x", 50, "1/250", 5200),
            ExposureBase("1x", 100, "1/250", 5200),
            ExposureBase("1x", 200, "1/250", 5200)
        )
    )
)

val allSceneFamilyInfos: List<SceneFamilyInfo> = sceneFamilies.mapIndexed { index, family ->
    SceneFamilyInfo(
        id = index + 1,
        name = family.name,
        category = family.category,
        bestUse = family.bestUse,
        baseTip = family.baseTip,
        presetCount = 18
    )
}

private fun generateAllPresets(): List<CameraPreset> {
    val list = ArrayList<CameraPreset>(1008)
    var currentId = 1

    for ((familyIndex, family) in sceneFamilies.withIndex()) {
        val fId = familyIndex + 1
        for (base in family.bases) {
            for (variant in controlVariants) {
                list.add(
                    CameraPreset(
                        id = currentId++,
                        familyId = fId,
                        familyName = family.name,
                        name = family.name,
                        category = family.category,
                        lens = base.lens,
                        iso = "ISO ${base.iso}",
                        isoNum = base.iso,
                        shutter = formatShutter(base.shutter),
                        shutterSeconds = parseShutterSeconds(base.shutter),
                        wb = "${base.wbKelvin}K",
                        wbKelvin = base.wbKelvin,
                        focus = variant.focus,
                        ev = variant.ev,
                        evValue = variant.evValue,
                        bestUse = family.bestUse,
                        tips = "${family.baseTip} ${variant.tipSuffix}",
                        meter = variant.meter,
                        variant = variant.name,
                        variantCode = variant.code
                    )
                )
            }
        }
    }
    return list
}

object PresetRepository {
    val presets: List<CameraPreset> = generateAllPresets()

    fun getById(id: Int): CameraPreset? = presets.getOrNull(id - 1)

    val quickDecisionRules = listOf(
        QuickDecisionRule("Bright sun", "ISO 50 → 1/500–1/1000 s → WB 5200–6000K", "Hard midday light, preserve cloud highlights with slight negative EV."),
        QuickDecisionRule("Normal daylight", "ISO 50 → 1/250–1/500 s → WB 5200–6000K", "Standard outdoor illumination with rich dynamic range."),
        QuickDecisionRule("People moving", "Shutter around 1/250 s or faster; raise ISO if necessary", "Motion blur on eyes and hands ruins portraits; shutter takes priority."),
        QuickDecisionRule("Cars moving", "1/1000 s to freeze action; 1/30–1/60 s for intentional panning blur", "Choose between surgical freeze or dynamic velocity streaks."),
        QuickDecisionRule("Night handheld", "Try 1/30–1/60 s first; raise ISO as needed", "Rely on OIS at 1/30s; push ISO to 400-800 rather than risking hand shake."),
        QuickDecisionRule("Night tripod", "ISO 50–100 and let shutter run longer", "Eliminate shadow noise entirely with locked phone on solid mount."),
        QuickDecisionRule("Moon", "Low ISO and relatively fast shutter (1/250s); meter for the moon, not sky", "The moon is in direct sunlight in outer space; do not overexpose!"),
        QuickDecisionRule("Stars", "Tripod + manual infinity focus + long exposure (20–30s)", "City light pollution is the limiting factor. Cool WB to 3600-3800K."),
        QuickDecisionRule("Water / light trails", "ISO 50 + tripod + slow shutter (1–5s)", "Silky flow or city light ribbons without blown highlights.")
    )

    val wbCheatSheet = listOf(
        WbItem("Sunny daylight", "5200–5600K", 5400),
        WbItem("Cloudy", "6000–6500K", 6250),
        WbItem("Golden hour", "6000–7000K", 6500),
        WbItem("Shade", "6000–7000K", 6500),
        WbItem("Fluorescent / cool indoor", "4000–4500K", 4250),
        WbItem("Warm restaurant / tungsten", "3000–3500K", 3250),
        WbItem("Night city", "3500–4200K", 3850),
        WbItem("Stars", "3500–4000K", 3750)
    )

    val s20UltraRules = listOf(
        S20Rule(
            title = "Prefer the Main 1x Camera for Cleanest Low-Light",
            description = "The main 108MP 1/1.33\" sensor has significantly larger light-gathering capability. The older telephoto hardware is more light-sensitive and may switch behavior depending on lighting."
        ),
        S20Rule(
            title = "Do Not Chase ISO 50 at Night",
            description = "A sharp ISO 400–800 photo is usually better than a dark ISO 50 frame that requires aggressive editing and risks hand motion blur."
        ),
        S20Rule(
            title = "For Moving People & Cars, Shutter Matters More",
            description = "Shutter speed freezes action. Motion blur cannot be fixed in Lightroom, while modest sensor noise at ISO 200–400 is easily cleaned."
        ),
        S20Rule(
            title = "For Tripod Scenes, ISO 50/100 is Preferred",
            description = "When mounted on a tripod, base ISO delivers pure dynamic range with 16-bit computational RAW precision."
        ),
        S20Rule(
            title = "RAW is Most Valuable When You Intend to Edit",
            description = "Samsung Expert RAW saves 16-bit linear DNG RAW data alongside JPEG, providing uncompressed control over shadows, highlights, and white balance."
        ),
        S20Rule(
            title = "Take Care with Long Continuous Bursts",
            description = "Samsung notes that shooting more than 10 photos continuously can slow down because of high-resolution RAW buffer processing."
        )
    )
}

data class QuickDecisionRule(val condition: String, val settings: String, val note: String)
data class WbItem(val lighting: String, val range: String, val kelvin: Int)
data class S20Rule(val title: String, val description: String)
