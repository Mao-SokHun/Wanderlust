package com.example.wanderlust.locale

import androidx.compose.runtime.Composable
import com.example.wanderlust.R

/** Localized label for known preset option values (DB keeps English keys). */
@Composable
fun optionLabel(value: String): String {
    if (value.isBlank()) return value
    AppLocale.code // recompose on language change
    return when (value) {
        // Cities
        "Phnom Penh" -> stringApp(R.string.city_phnom_penh)
        "Siem Reap" -> stringApp(R.string.city_siem_reap)
        "Sihanoukville" -> stringApp(R.string.city_sihanoukville)
        "Battambang" -> stringApp(R.string.city_battambang)
        "Kampot" -> stringApp(R.string.city_kampot)
        "Kep" -> stringApp(R.string.city_kep)
        "Koh Kong" -> stringApp(R.string.city_koh_kong)
        "Kratie" -> stringApp(R.string.city_kratie)
        "Mondulkiri" -> stringApp(R.string.city_mondulkiri)
        "Poipet" -> stringApp(R.string.city_poipet)
        "Banlung" -> stringApp(R.string.city_banlung)

        // Rental types
        "SUV / Crossover (5 seats)" -> stringApp(R.string.opt_rental_suv)
        "Minivan / MPV (7–9 seats)", "Minivan / MPV (7-9 seats)" -> stringApp(R.string.opt_rental_minivan)
        "VIP Van (12–15 seats)", "VIP Van (12-15 seats)" -> stringApp(R.string.opt_rental_vip)

        // Rental exclusions / docs / features
        "Fuel (guest pays)" -> stringApp(R.string.opt_excl_fuel)
        "Expressway tolls" -> stringApp(R.string.opt_excl_toll)
        "Driver meals & lodging (multi-day)" -> stringApp(R.string.opt_excl_driver_meals)
        "National ID (copy or original)" -> stringApp(R.string.opt_doc_id)
        "Valid driving license" -> stringApp(R.string.opt_doc_license)
        "Security deposit" -> stringApp(R.string.opt_doc_deposit)
        "Dash camera" -> stringApp(R.string.opt_feat_dashcam)
        "Bluetooth / Apple CarPlay" -> stringApp(R.string.opt_feat_carplay)
        "Stability control" -> stringApp(R.string.opt_feat_stability)
        "Strong A/C" -> stringApp(R.string.opt_feat_ac)
        "Spacious cabin" -> stringApp(R.string.opt_feat_cabin)
        "Baby car seat" -> stringApp(R.string.opt_addon_seat)
        "Roof rack" -> stringApp(R.string.opt_addon_rack)
        "Khmer" -> stringApp(R.string.opt_lang_khmer)
        "English" -> stringApp(R.string.opt_lang_english)
        "Chinese" -> stringApp(R.string.opt_lang_chinese)
        "Full to Full" -> stringApp(R.string.opt_fuel_full)
        "With driver" -> stringApp(R.string.rental_with_driver)
        "Self-drive" -> stringApp(R.string.rental_self_drive)

        // Trip vehicles / amenities
        "VIP Van / Minivan" -> stringApp(R.string.opt_trip_vip_van)
        "Seat Bus" -> stringApp(R.string.opt_trip_seat_bus)
        "Sleeper Bus" -> stringApp(R.string.opt_trip_sleeper)
        "Sedan / SUV" -> stringApp(R.string.opt_trip_sedan)
        "Free Wi-Fi" -> stringApp(R.string.opt_amenity_wifi)
        "USB Charging Ports" -> stringApp(R.string.opt_amenity_usb)
        "Free Water & Wet Tissue" -> stringApp(R.string.opt_amenity_water)
        "Air Conditioning" -> stringApp(R.string.opt_amenity_ac)
        "Restroom on Board" -> stringApp(R.string.opt_amenity_restroom)
        "Passenger Insurance" -> stringApp(R.string.opt_amenity_insurance)
        "ford_15" -> stringApp(R.string.opt_layout_ford)
        "bus_2_aisle" -> stringApp(R.string.opt_layout_bus)
        "sleeper_2deck" -> stringApp(R.string.opt_layout_sleeper)
        "suv_7" -> stringApp(R.string.opt_layout_suv)

        // Tour package presets
        "Adventure" -> stringApp(R.string.opt_tour_adventure)
        "Relax" -> stringApp(R.string.opt_tour_relax)
        "Camping" -> stringApp(R.string.opt_tour_camping)
        "Culture" -> stringApp(R.string.opt_tour_culture)
        "History" -> stringApp(R.string.opt_tour_history)
        "Temple" -> stringApp(R.string.opt_tour_temple)
        "Beach" -> stringApp(R.string.opt_tour_beach)
        "Nature" -> stringApp(R.string.opt_tour_nature)
        "Food" -> stringApp(R.string.opt_tour_food)
        "City" -> stringApp(R.string.opt_tour_city)
        "VIP tour van" -> stringApp(R.string.opt_transport_vip)
        "Bus" -> stringApp(R.string.opt_transport_bus)
        "Private car" -> stringApp(R.string.opt_transport_car)
        "Pick-up & drop-off" -> stringApp(R.string.opt_transport_pickup)
        "Hotel" -> stringApp(R.string.opt_acc_hotel)
        "Resort" -> stringApp(R.string.opt_acc_resort)
        "Homestay" -> stringApp(R.string.opt_acc_homestay)
        "Breakfast" -> stringApp(R.string.opt_meal_breakfast)
        "Lunch" -> stringApp(R.string.opt_meal_lunch)
        "Dinner" -> stringApp(R.string.opt_meal_dinner)
        "Drinking water" -> stringApp(R.string.opt_meal_water)
        "Cold towels" -> stringApp(R.string.opt_meal_towels)
        "Entrance tickets" -> stringApp(R.string.opt_act_entrance)
        "Boat ticket" -> stringApp(R.string.opt_act_boat)
        "Protected area ticket" -> stringApp(R.string.opt_act_park)
        "Local tour guide" -> stringApp(R.string.opt_act_guide)
        "Personal expenses" -> stringApp(R.string.opt_excl_personal)
        "Meals not listed in the program" -> stringApp(R.string.opt_excl_meals)
        "Tips for driver / guide" -> stringApp(R.string.opt_excl_tips)
        "Long pants for temples / palace" -> stringApp(R.string.opt_bring_pants)
        "Hiking shoes" -> stringApp(R.string.opt_bring_shoes)
        "Personal medicine" -> stringApp(R.string.opt_bring_medicine)
        "Sunscreen & hat" -> stringApp(R.string.opt_bring_sunscreen)
        "Light jacket" -> stringApp(R.string.opt_bring_jacket)

        // Help topics
        "App crash / bug" -> stringApp(R.string.help_topic_crash)
        "Login / account" -> stringApp(R.string.help_topic_login)
        "Business / subscription" -> stringApp(R.string.help_topic_business)
        "Listing / booking issue" -> stringApp(R.string.help_topic_listing)
        "Other" -> stringApp(R.string.help_topic_other)

        else -> value
    }
}

@Composable
fun optionLabels(values: List<String>): Map<String, String> =
    values.associateWith { optionLabel(it) }
