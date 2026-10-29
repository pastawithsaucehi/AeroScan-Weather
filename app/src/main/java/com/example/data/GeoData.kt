package com.example.data

import com.example.model.GeoPoint
import com.example.model.IsobarLine

/**
 * High-fidelity, lightweight world coastline and topography vector data.
 * Optimized for crisp, high-contrast terrain rendering on mobile screens.
 */
object GeoData {

    // Main continents and landmasses with high geographic fidelity
    val continents: List<List<GeoPoint>> = listOf(
        // North America (including Alaska, Canada, USA, Mexico, Central America)
        listOf(
            GeoPoint(71.3, -156.8), GeoPoint(70.5, -161.8), GeoPoint(66.0, -168.0),
            GeoPoint(64.5, -165.4), GeoPoint(60.0, -166.0), GeoPoint(58.5, -161.5),
            GeoPoint(55.0, -163.0), GeoPoint(57.0, -154.0), GeoPoint(60.0, -148.0),
            GeoPoint(59.5, -139.7), GeoPoint(55.0, -131.5), GeoPoint(51.0, -128.0),
            GeoPoint(48.5, -124.7), GeoPoint(45.0, -124.0), GeoPoint(40.4, -124.4),
            GeoPoint(37.8, -122.5), GeoPoint(34.0, -119.0), GeoPoint(32.5, -117.1),
            GeoPoint(28.0, -114.5), GeoPoint(23.0, -110.0), GeoPoint(24.5, -110.5),
            GeoPoint(29.0, -113.5), GeoPoint(31.8, -114.8), GeoPoint(29.0, -112.0),
            GeoPoint(24.0, -107.5), GeoPoint(20.0, -105.3), GeoPoint(16.0, -98.0),
            GeoPoint(14.5, -92.2), GeoPoint(9.0, -84.0), GeoPoint(8.0, -82.0),
            GeoPoint(7.8, -77.5), GeoPoint(9.5, -79.0), GeoPoint(11.0, -84.0),
            GeoPoint(15.0, -83.5), GeoPoint(16.0, -86.0), GeoPoint(16.0, -89.0),
            GeoPoint(18.5, -88.0), GeoPoint(21.5, -87.0), GeoPoint(19.8, -90.5),
            GeoPoint(18.5, -93.0), GeoPoint(22.0, -97.8), GeoPoint(26.0, -97.1),
            GeoPoint(29.0, -95.0), GeoPoint(29.5, -93.8), GeoPoint(29.2, -89.4),
            GeoPoint(30.4, -87.5), GeoPoint(30.0, -84.0), GeoPoint(28.0, -82.8),
            GeoPoint(25.0, -81.0), GeoPoint(25.7, -80.1), GeoPoint(27.5, -80.3),
            GeoPoint(31.0, -81.4), GeoPoint(33.5, -79.0), GeoPoint(35.2, -75.5),
            GeoPoint(38.0, -75.2), GeoPoint(40.5, -74.0), GeoPoint(41.5, -71.0),
            GeoPoint(44.5, -66.0), GeoPoint(45.0, -61.0), GeoPoint(48.0, -64.5),
            GeoPoint(51.5, -56.0), GeoPoint(58.0, -62.5), GeoPoint(60.5, -64.8),
            GeoPoint(58.0, -69.0), GeoPoint(62.0, -75.0), GeoPoint(55.0, -80.0),
            GeoPoint(51.5, -80.5), GeoPoint(54.0, -89.0), GeoPoint(60.0, -94.0),
            GeoPoint(64.0, -88.0), GeoPoint(68.0, -90.0), GeoPoint(69.0, -115.0),
            GeoPoint(70.0, -130.0), GeoPoint(69.5, -141.0), GeoPoint(71.3, -156.8)
        ),

        // South America
        listOf(
            GeoPoint(12.5, -71.7), GeoPoint(11.0, -74.8), GeoPoint(8.0, -77.0),
            GeoPoint(4.0, -77.5), GeoPoint(1.5, -79.0), GeoPoint(-2.5, -80.8),
            GeoPoint(-5.0, -81.2), GeoPoint(-9.0, -78.6), GeoPoint(-14.0, -76.3),
            GeoPoint(-18.5, -70.3), GeoPoint(-23.5, -70.4), GeoPoint(-33.0, -71.6),
            GeoPoint(-41.5, -73.8), GeoPoint(-46.0, -75.2), GeoPoint(-53.0, -74.0),
            GeoPoint(-55.0, -67.0), GeoPoint(-54.0, -65.0), GeoPoint(-50.0, -68.0),
            GeoPoint(-46.0, -65.5), GeoPoint(-42.0, -63.0), GeoPoint(-38.0, -57.5),
            GeoPoint(-35.0, -56.0), GeoPoint(-34.5, -58.4), GeoPoint(-32.0, -52.0),
            GeoPoint(-28.0, -48.6), GeoPoint(-23.0, -43.2), GeoPoint(-20.5, -40.3),
            GeoPoint(-18.0, -39.5), GeoPoint(-13.0, -38.5), GeoPoint(-9.0, -35.2),
            GeoPoint(-5.5, -35.2), GeoPoint(-3.0, -41.0), GeoPoint(-1.5, -44.5),
            GeoPoint(-0.5, -50.0), GeoPoint(2.5, -51.0), GeoPoint(5.0, -52.5),
            GeoPoint(6.8, -58.2), GeoPoint(10.5, -61.8), GeoPoint(10.8, -68.2),
            GeoPoint(11.8, -71.5), GeoPoint(12.5, -71.7)
        ),

        // Europe (including Scandinavia, Iberia, Italy, Greece, Eastern Europe)
        listOf(
            GeoPoint(36.0, -5.6), GeoPoint(37.0, -9.0), GeoPoint(41.0, -8.7),
            GeoPoint(43.5, -9.3), GeoPoint(43.5, -1.8), GeoPoint(46.0, -1.2),
            GeoPoint(48.5, -4.7), GeoPoint(49.5, -0.5), GeoPoint(51.0, 2.5),
            GeoPoint(53.5, 7.0), GeoPoint(55.5, 8.5), GeoPoint(57.5, 10.5),
            GeoPoint(55.5, 12.5), GeoPoint(56.0, 15.0), GeoPoint(60.0, 18.8),
            GeoPoint(65.5, 22.0), GeoPoint(65.0, 25.5), GeoPoint(60.5, 28.0),
            GeoPoint(60.0, 24.5), GeoPoint(59.0, 18.0), GeoPoint(56.0, 13.0),
            GeoPoint(58.0, 6.0), GeoPoint(62.0, 5.0), GeoPoint(68.0, 14.0),
            GeoPoint(71.0, 26.0), GeoPoint(70.0, 31.0), GeoPoint(68.0, 39.0),
            GeoPoint(65.0, 41.0), GeoPoint(64.5, 38.0), GeoPoint(67.0, 44.0),
            GeoPoint(68.0, 50.0), GeoPoint(67.0, 60.0), GeoPoint(60.0, 60.0),
            GeoPoint(50.0, 50.0), GeoPoint(46.5, 48.0), GeoPoint(45.0, 38.0),
            GeoPoint(46.5, 31.0), GeoPoint(44.0, 29.0), GeoPoint(41.5, 28.5),
            GeoPoint(40.0, 23.0), GeoPoint(38.0, 23.5), GeoPoint(36.5, 22.0),
            GeoPoint(38.5, 21.0), GeoPoint(40.5, 19.5), GeoPoint(44.0, 15.0),
            GeoPoint(45.5, 13.5), GeoPoint(42.0, 15.0), GeoPoint(40.5, 17.5),
            GeoPoint(38.0, 16.0), GeoPoint(39.0, 16.0), GeoPoint(41.0, 14.0),
            GeoPoint(44.0, 10.0), GeoPoint(43.5, 7.0), GeoPoint(43.0, 3.0),
            GeoPoint(41.5, 2.5), GeoPoint(39.0, -0.3), GeoPoint(36.7, -2.5),
            GeoPoint(36.0, -5.6)
        ),

        // Africa
        listOf(
            GeoPoint(36.0, -5.5), GeoPoint(35.5, -1.0), GeoPoint(37.0, 10.0),
            GeoPoint(34.0, 11.0), GeoPoint(33.0, 14.0), GeoPoint(32.0, 20.0),
            GeoPoint(32.0, 24.5), GeoPoint(31.5, 31.5), GeoPoint(30.0, 32.5),
            GeoPoint(27.5, 34.0), GeoPoint(22.0, 37.0), GeoPoint(15.5, 40.0),
            GeoPoint(12.5, 43.5), GeoPoint(11.5, 51.0), GeoPoint(8.0, 50.0),
            GeoPoint(4.0, 47.0), GeoPoint(1.0, 44.0), GeoPoint(-2.0, 41.0),
            GeoPoint(-6.0, 39.0), GeoPoint(-10.5, 40.5), GeoPoint(-16.0, 40.0),
            GeoPoint(-22.0, 35.5), GeoPoint(-26.0, 33.0), GeoPoint(-28.0, 32.5),
            GeoPoint(-33.0, 27.5), GeoPoint(-34.5, 20.0), GeoPoint(-34.0, 18.5),
            GeoPoint(-31.0, 17.8), GeoPoint(-28.5, 16.5), GeoPoint(-22.0, 14.5),
            GeoPoint(-16.0, 11.8), GeoPoint(-12.5, 13.5), GeoPoint(-8.5, 13.0),
            GeoPoint(-5.0, 12.0), GeoPoint(-1.0, 9.0), GeoPoint(4.0, 9.0),
            GeoPoint(4.5, 6.0), GeoPoint(6.0, 2.0), GeoPoint(5.0, -1.0),
            GeoPoint(4.5, -7.5), GeoPoint(7.0, -12.0), GeoPoint(9.5, -13.5),
            GeoPoint(12.0, -16.0), GeoPoint(14.8, -17.5), GeoPoint(18.0, -16.0),
            GeoPoint(21.0, -17.2), GeoPoint(25.0, -15.0), GeoPoint(28.0, -12.5),
            GeoPoint(31.0, -10.0), GeoPoint(34.0, -7.0), GeoPoint(36.0, -5.5)
        ),

        // Asia & Middle East
        listOf(
            GeoPoint(41.5, 29.0), GeoPoint(42.0, 35.0), GeoPoint(41.5, 41.5),
            GeoPoint(38.0, 46.5), GeoPoint(31.0, 49.0), GeoPoint(30.0, 48.0),
            GeoPoint(26.5, 50.0), GeoPoint(24.0, 52.0), GeoPoint(24.0, 56.5),
            GeoPoint(22.5, 59.8), GeoPoint(19.0, 57.5), GeoPoint(17.0, 54.0),
            GeoPoint(14.5, 49.0), GeoPoint(12.8, 45.0), GeoPoint(13.0, 43.0),
            GeoPoint(16.0, 42.0), GeoPoint(22.0, 39.0), GeoPoint(28.0, 35.0),
            GeoPoint(31.5, 34.5), GeoPoint(36.0, 36.0), GeoPoint(37.0, 38.0),
            GeoPoint(36.5, 42.0), GeoPoint(30.0, 48.0), GeoPoint(27.0, 56.5),
            GeoPoint(25.5, 61.5), GeoPoint(25.0, 66.5), GeoPoint(23.0, 68.5),
            GeoPoint(21.0, 70.0), GeoPoint(21.5, 72.8), GeoPoint(19.0, 72.8),
            GeoPoint(15.0, 74.0), GeoPoint(10.0, 76.0), GeoPoint(8.0, 77.5),
            GeoPoint(10.0, 79.8), GeoPoint(13.0, 80.3), GeoPoint(17.0, 82.5),
            GeoPoint(21.0, 87.0), GeoPoint(22.0, 91.0), GeoPoint(20.5, 92.8),
            GeoPoint(16.0, 94.5), GeoPoint(16.0, 97.5), GeoPoint(12.0, 99.0),
            GeoPoint(7.0, 100.0), GeoPoint(1.3, 103.8), GeoPoint(4.0, 103.5),
            GeoPoint(7.0, 101.5), GeoPoint(10.0, 99.5), GeoPoint(12.5, 101.0),
            GeoPoint(10.5, 104.5), GeoPoint(8.5, 105.0), GeoPoint(11.0, 108.5),
            GeoPoint(16.0, 108.2), GeoPoint(21.0, 108.0), GeoPoint(21.5, 110.0),
            GeoPoint(22.3, 114.2), GeoPoint(24.5, 118.5), GeoPoint(28.0, 121.5),
            GeoPoint(31.5, 121.5), GeoPoint(35.0, 119.5), GeoPoint(37.5, 122.0),
            GeoPoint(39.0, 123.0), GeoPoint(39.0, 127.5), GeoPoint(35.0, 129.5),
            GeoPoint(37.5, 131.0), GeoPoint(42.5, 130.5), GeoPoint(43.0, 133.0),
            GeoPoint(47.5, 139.0), GeoPoint(53.0, 141.0), GeoPoint(56.0, 137.5),
            GeoPoint(59.5, 150.0), GeoPoint(62.0, 160.0), GeoPoint(60.0, 165.0),
            GeoPoint(56.0, 163.0), GeoPoint(51.0, 156.5), GeoPoint(58.0, 162.0),
            GeoPoint(65.0, 172.0), GeoPoint(67.0, 178.0), GeoPoint(70.0, 178.0),
            GeoPoint(70.0, 160.0), GeoPoint(72.0, 140.0), GeoPoint(74.0, 115.0),
            GeoPoint(76.5, 100.0), GeoPoint(73.0, 80.0), GeoPoint(70.0, 60.0),
            GeoPoint(67.0, 60.0), GeoPoint(60.0, 60.0), GeoPoint(50.0, 50.0),
            GeoPoint(42.0, 48.0), GeoPoint(40.0, 48.0), GeoPoint(38.0, 44.0),
            GeoPoint(41.5, 29.0)
        ),

        // Australia & Tasmania
        listOf(
            GeoPoint(-12.0, 131.0), GeoPoint(-11.5, 136.0), GeoPoint(-15.0, 136.5),
            GeoPoint(-14.0, 141.5), GeoPoint(-10.8, 142.5), GeoPoint(-14.0, 144.5),
            GeoPoint(-18.0, 146.5), GeoPoint(-23.0, 151.0), GeoPoint(-28.0, 153.5),
            GeoPoint(-33.0, 151.5), GeoPoint(-37.5, 150.0), GeoPoint(-39.0, 146.5),
            GeoPoint(-38.0, 141.0), GeoPoint(-36.0, 137.0), GeoPoint(-32.0, 132.5),
            GeoPoint(-32.0, 126.0), GeoPoint(-34.0, 123.0), GeoPoint(-35.0, 117.0),
            GeoPoint(-32.0, 115.5), GeoPoint(-26.0, 113.0), GeoPoint(-22.0, 114.0),
            GeoPoint(-20.0, 119.0), GeoPoint(-16.5, 123.0), GeoPoint(-14.0, 126.0),
            GeoPoint(-14.5, 129.5), GeoPoint(-12.0, 131.0)
        ),

        // Antarctica (Polar Ice continent)
        listOf(
            GeoPoint(-63.5, -57.0), GeoPoint(-67.0, -68.0), GeoPoint(-72.0, -80.0),
            GeoPoint(-74.0, -100.0), GeoPoint(-75.0, -140.0), GeoPoint(-78.0, -165.0),
            GeoPoint(-72.0, 170.0), GeoPoint(-66.0, 140.0), GeoPoint(-66.0, 110.0),
            GeoPoint(-65.0, 80.0), GeoPoint(-67.0, 50.0), GeoPoint(-70.0, 20.0),
            GeoPoint(-70.0, -10.0), GeoPoint(-75.0, -30.0), GeoPoint(-63.5, -57.0)
        )
    )

    // Major Islands that are critical for tropical cyclone & global weather tracking
    val islands: List<List<GeoPoint>> = listOf(
        // United Kingdom & Ireland
        listOf(
            GeoPoint(58.5, -4.5), GeoPoint(57.5, -1.8), GeoPoint(54.0, 0.0),
            GeoPoint(51.0, 1.4), GeoPoint(50.0, -5.2), GeoPoint(52.0, -5.0),
            GeoPoint(54.5, -3.5), GeoPoint(56.0, -6.0), GeoPoint(58.5, -4.5)
        ),
        // Ireland
        listOf(
            GeoPoint(55.2, -7.5), GeoPoint(54.0, -6.0), GeoPoint(52.2, -6.3),
            GeoPoint(51.5, -9.5), GeoPoint(53.0, -10.0), GeoPoint(54.5, -9.0),
            GeoPoint(55.2, -7.5)
        ),
        // Japan (Honshu / Hokkaido / Kyushu)
        listOf(
            GeoPoint(45.5, 142.0), GeoPoint(43.5, 145.5), GeoPoint(42.0, 141.0),
            GeoPoint(40.5, 141.8), GeoPoint(36.0, 140.5), GeoPoint(34.5, 137.0),
            GeoPoint(33.5, 135.5), GeoPoint(31.0, 130.5), GeoPoint(33.0, 129.8),
            GeoPoint(35.5, 134.0), GeoPoint(38.0, 138.5), GeoPoint(41.5, 140.5),
            GeoPoint(45.5, 142.0)
        ),
        // Taiwan
        listOf(
            GeoPoint(25.3, 121.5), GeoPoint(24.5, 122.0), GeoPoint(22.0, 120.9),
            GeoPoint(22.5, 120.3), GeoPoint(25.0, 121.0), GeoPoint(25.3, 121.5)
        ),
        // Philippines (Luzon & Mindanao)
        listOf(
            GeoPoint(18.5, 121.5), GeoPoint(16.5, 122.4), GeoPoint(13.5, 124.0),
            GeoPoint(12.5, 122.0), GeoPoint(14.5, 120.5), GeoPoint(18.5, 121.5)
        ),
        listOf(
            GeoPoint(9.8, 125.5), GeoPoint(7.0, 126.5), GeoPoint(5.5, 125.0),
            GeoPoint(7.0, 122.0), GeoPoint(8.5, 124.5), GeoPoint(9.8, 125.5)
        ),
        // Cuba (Crucial for Atlantic / Caribbean Hurricanes)
        listOf(
            GeoPoint(23.2, -81.0), GeoPoint(22.0, -78.0), GeoPoint(20.0, -75.0),
            GeoPoint(20.0, -77.0), GeoPoint(22.0, -84.0), GeoPoint(23.0, -84.5),
            GeoPoint(23.2, -81.0)
        ),
        // Hispaniola (Dominican Rep / Haiti)
        listOf(
            GeoPoint(19.9, -71.0), GeoPoint(18.5, -68.5), GeoPoint(18.2, -71.5),
            GeoPoint(18.5, -74.5), GeoPoint(19.8, -72.8), GeoPoint(19.9, -71.0)
        ),
        // Madagascar
        listOf(
            GeoPoint(-12.0, 49.3), GeoPoint(-15.5, 50.5), GeoPoint(-25.0, 47.0),
            GeoPoint(-25.5, 45.0), GeoPoint(-20.0, 43.5), GeoPoint(-15.0, 46.5),
            GeoPoint(-12.0, 49.3)
        ),
        // New Zealand (North & South Island)
        listOf(
            GeoPoint(-34.5, 172.7), GeoPoint(-37.5, 178.5), GeoPoint(-41.3, 174.8),
            GeoPoint(-39.0, 174.0), GeoPoint(-37.0, 174.5), GeoPoint(-34.5, 172.7)
        ),
        listOf(
            GeoPoint(-40.5, 173.0), GeoPoint(-43.5, 173.5), GeoPoint(-46.5, 169.0),
            GeoPoint(-46.0, 166.5), GeoPoint(-41.5, 171.5), GeoPoint(-40.5, 173.0)
        ),
        // Greenland
        listOf(
            GeoPoint(83.0, -35.0), GeoPoint(76.0, -18.0), GeoPoint(65.0, -37.0),
            GeoPoint(60.0, -43.5), GeoPoint(65.0, -53.0), GeoPoint(76.0, -68.0),
            GeoPoint(82.0, -60.0), GeoPoint(83.0, -35.0)
        ),
        // Iceland
        listOf(
            GeoPoint(66.5, -16.0), GeoPoint(64.5, -13.5), GeoPoint(63.5, -19.0),
            GeoPoint(64.5, -24.0), GeoPoint(66.5, -23.0), GeoPoint(66.5, -16.0)
        ),

        // --- INDONESIAN ARCHIPELAGO ---
        // Sumatra (Indonesia)
        listOf(
            GeoPoint(5.6, 95.3), GeoPoint(5.3, 96.0), GeoPoint(4.5, 97.8),
            GeoPoint(3.6, 98.7), GeoPoint(2.5, 100.0), GeoPoint(1.5, 102.1),
            GeoPoint(0.5, 103.5), GeoPoint(-0.8, 104.5), GeoPoint(-2.5, 105.8),
            GeoPoint(-3.5, 106.0), GeoPoint(-5.5, 105.9), GeoPoint(-5.9, 105.8),
            GeoPoint(-5.8, 104.5), GeoPoint(-5.1, 104.0), GeoPoint(-4.2, 102.8),
            GeoPoint(-3.0, 101.5), GeoPoint(-1.5, 100.3), GeoPoint(-0.2, 98.8),
            GeoPoint(1.5, 97.4), GeoPoint(2.8, 96.8), GeoPoint(4.2, 96.1),
            GeoPoint(5.6, 95.3)
        ),
        // Java (Indonesia)
        listOf(
            GeoPoint(-6.0, 105.8), GeoPoint(-6.1, 106.9), GeoPoint(-6.3, 108.0),
            GeoPoint(-6.7, 108.6), GeoPoint(-6.9, 110.0), GeoPoint(-6.6, 110.7),
            GeoPoint(-6.9, 111.5), GeoPoint(-6.9, 112.6), GeoPoint(-7.2, 112.8),
            GeoPoint(-7.7, 114.0), GeoPoint(-7.7, 114.4), GeoPoint(-8.7, 114.5),
            GeoPoint(-8.6, 113.5), GeoPoint(-8.3, 112.0), GeoPoint(-8.2, 111.0),
            GeoPoint(-7.8, 109.0), GeoPoint(-7.7, 108.5), GeoPoint(-7.0, 106.5),
            GeoPoint(-6.8, 105.2), GeoPoint(-6.0, 105.8)
        ),
        // Madura (Indonesia)
        listOf(
            GeoPoint(-6.9, 112.7), GeoPoint(-6.9, 114.0), GeoPoint(-7.2, 114.0),
            GeoPoint(-7.2, 112.8), GeoPoint(-6.9, 112.7)
        ),
        // Borneo / Kalimantan (Indonesia)
        listOf(
            GeoPoint(7.0, 116.8), GeoPoint(6.0, 117.8), GeoPoint(4.5, 118.5),
            GeoPoint(4.2, 117.8), GeoPoint(3.5, 117.6), GeoPoint(2.0, 117.9),
            GeoPoint(0.5, 117.6), GeoPoint(-1.2, 117.0), GeoPoint(-2.5, 116.6),
            GeoPoint(-3.8, 116.3), GeoPoint(-4.2, 114.6), GeoPoint(-3.5, 113.0),
            GeoPoint(-3.4, 111.7), GeoPoint(-2.8, 110.1), GeoPoint(-1.5, 109.8),
            GeoPoint(-0.1, 109.2), GeoPoint(1.0, 109.0), GeoPoint(2.0, 109.7),
            GeoPoint(3.0, 111.0), GeoPoint(4.0, 113.5), GeoPoint(4.9, 115.0),
            GeoPoint(6.0, 115.8), GeoPoint(7.0, 116.8)
        ),
        // Sulawesi / Celebes (Indonesia)
        listOf(
            GeoPoint(1.5, 125.0), GeoPoint(0.8, 123.0), GeoPoint(0.5, 120.5),
            GeoPoint(1.0, 120.0), GeoPoint(-0.8, 119.8), GeoPoint(-2.5, 118.9),
            GeoPoint(-3.5, 119.0), GeoPoint(-5.3, 119.5), GeoPoint(-5.6, 120.4),
            GeoPoint(-4.0, 120.3), GeoPoint(-2.8, 120.5), GeoPoint(-2.5, 121.2),
            GeoPoint(-4.0, 121.5), GeoPoint(-5.3, 122.8), GeoPoint(-4.5, 123.0),
            GeoPoint(-3.0, 122.2), GeoPoint(-1.5, 121.5), GeoPoint(-1.0, 123.5),
            GeoPoint(-1.5, 123.5), GeoPoint(-0.5, 121.5), GeoPoint(0.5, 121.8),
            GeoPoint(1.0, 124.5), GeoPoint(1.5, 125.0)
        ),
        // Papua / New Guinea (Indonesia West Papua & Papua)
        listOf(
            GeoPoint(-0.9, 131.3), GeoPoint(-0.8, 134.0), GeoPoint(-1.8, 134.2),
            GeoPoint(-2.5, 135.0), GeoPoint(-2.5, 140.7), GeoPoint(-3.0, 142.5),
            GeoPoint(-5.5, 146.0), GeoPoint(-8.5, 148.0), GeoPoint(-10.5, 150.5),
            GeoPoint(-10.0, 149.0), GeoPoint(-9.5, 147.2), GeoPoint(-8.5, 143.5),
            GeoPoint(-8.5, 140.4), GeoPoint(-7.5, 138.5), GeoPoint(-4.5, 136.0),
            GeoPoint(-3.5, 134.5), GeoPoint(-2.5, 132.5), GeoPoint(-2.0, 131.0),
            GeoPoint(-0.9, 131.3)
        ),
        // Bali (Indonesia)
        listOf(
            GeoPoint(-8.1, 114.4), GeoPoint(-8.3, 115.5), GeoPoint(-8.8, 115.2),
            GeoPoint(-8.4, 114.5), GeoPoint(-8.1, 114.4)
        ),
        // Lombok (Indonesia)
        listOf(
            GeoPoint(-8.3, 116.0), GeoPoint(-8.4, 116.7), GeoPoint(-8.9, 116.3),
            GeoPoint(-8.7, 115.9), GeoPoint(-8.3, 116.0)
        ),
        // Sumbawa (Indonesia)
        listOf(
            GeoPoint(-8.3, 116.8), GeoPoint(-8.3, 119.0), GeoPoint(-8.9, 118.5),
            GeoPoint(-8.9, 117.0), GeoPoint(-8.3, 116.8)
        ),
        // Flores (Indonesia)
        listOf(
            GeoPoint(-8.4, 119.9), GeoPoint(-8.3, 123.0), GeoPoint(-8.8, 122.5),
            GeoPoint(-8.8, 120.0), GeoPoint(-8.4, 119.9)
        ),
        // Sumba (Indonesia)
        listOf(
            GeoPoint(-9.4, 119.0), GeoPoint(-9.3, 120.5), GeoPoint(-10.2, 120.8),
            GeoPoint(-10.0, 119.2), GeoPoint(-9.4, 119.0)
        ),
        // Timor (West Timor Indonesia & East Timor)
        listOf(
            GeoPoint(-10.3, 123.5), GeoPoint(-8.4, 127.3), GeoPoint(-8.8, 127.0),
            GeoPoint(-10.2, 124.0), GeoPoint(-10.3, 123.5)
        ),
        // Halmahera (Maluku - Indonesia)
        listOf(
            GeoPoint(1.8, 127.8), GeoPoint(1.5, 128.8), GeoPoint(0.5, 128.5),
            GeoPoint(0.0, 128.0), GeoPoint(0.5, 127.5), GeoPoint(1.8, 127.8)
        ),
        // Seram & Ambon (Maluku - Indonesia)
        listOf(
            GeoPoint(-2.9, 128.0), GeoPoint(-3.2, 130.8), GeoPoint(-3.8, 130.0),
            GeoPoint(-3.7, 128.0), GeoPoint(-2.9, 128.0)
        ),
        // Buru (Maluku - Indonesia)
        listOf(
            GeoPoint(-3.1, 126.1), GeoPoint(-3.2, 127.2), GeoPoint(-3.8, 127.0),
            GeoPoint(-3.8, 126.2), GeoPoint(-3.1, 126.1)
        )
    )

    // Major mountain relief & topography ridges for tactical terrain elevation
    val topographyRidges: List<List<GeoPoint>> = listOf(
        // Rocky Mountains
        listOf(GeoPoint(58.0, -125.0), GeoPoint(50.0, -116.0), GeoPoint(40.0, -106.0), GeoPoint(32.0, -104.0)),
        // Andes Cordillera
        listOf(GeoPoint(8.0, -74.0), GeoPoint(-2.0, -78.0), GeoPoint(-15.0, -72.0), GeoPoint(-30.0, -70.0), GeoPoint(-45.0, -72.0)),
        // Himalayas
        listOf(GeoPoint(34.0, 75.0), GeoPoint(30.0, 81.0), GeoPoint(28.0, 88.0), GeoPoint(28.0, 96.0)),
        // Alps
        listOf(GeoPoint(45.0, 6.0), GeoPoint(46.5, 8.5), GeoPoint(47.0, 11.5), GeoPoint(46.0, 14.0)),
        // Bukit Barisan (Sumatra Volcanic Arc)
        listOf(GeoPoint(5.0, 96.0), GeoPoint(2.5, 98.8), GeoPoint(-0.5, 100.5), GeoPoint(-3.0, 102.5), GeoPoint(-5.0, 104.5)),
        // Java Volcanic Spine (Merapi, Semeru, Bromo)
        listOf(GeoPoint(-6.8, 106.8), GeoPoint(-7.2, 109.5), GeoPoint(-7.6, 110.5), GeoPoint(-8.0, 112.5), GeoPoint(-8.2, 114.0)),
        // Jayawijaya Mountains / Sudirman Range (Puncak Jaya 4,884m - Papua)
        listOf(GeoPoint(-3.8, 135.5), GeoPoint(-4.1, 137.2), GeoPoint(-4.4, 139.0), GeoPoint(-4.8, 141.0))
    )

    // Prominent continent label coordinates for tactical radar map
    data class MapLabel(val text: String, val point: GeoPoint)

    val continentLabels: List<MapLabel> = listOf(
        MapLabel("NORTH AMERICA", GeoPoint(42.0, -100.0)),
        MapLabel("SOUTH AMERICA", GeoPoint(-15.0, -60.0)),
        MapLabel("EUROPE", GeoPoint(52.0, 15.0)),
        MapLabel("AFRICA", GeoPoint(8.0, 22.0)),
        MapLabel("ASIA", GeoPoint(45.0, 95.0)),
        MapLabel("AUSTRALIA", GeoPoint(-25.0, 134.0)),
        MapLabel("GREENLAND", GeoPoint(72.0, -42.0)),
        MapLabel("ATLANTIC OCEAN", GeoPoint(20.0, -40.0)),
        MapLabel("PACIFIC OCEAN", GeoPoint(15.0, -160.0)),
        MapLabel("INDIAN OCEAN", GeoPoint(-15.0, 75.0)),
        MapLabel("INDONESIA", GeoPoint(-2.2, 117.5)),
        MapLabel("JAKARTA", GeoPoint(-6.2, 106.8)),
        MapLabel("SURABAYA", GeoPoint(-7.2, 112.7)),
        MapLabel("MEDAN", GeoPoint(3.6, 98.7)),
        MapLabel("MAKASSAR", GeoPoint(-5.1, 119.4)),
        MapLabel("BALI", GeoPoint(-8.5, 115.2)),
        MapLabel("PAPUA", GeoPoint(-4.2, 138.5))
    )

    // Synoptic barometric pressure isobars
    val synopticIsobars: List<IsobarLine> = listOf(
        // Isobars around Hurricane Arthur (2026) (Central Pressure 922 hPa)
        IsobarLine(960, listOf(GeoPoint(26.2, -87.8), GeoPoint(25.5, -85.0), GeoPoint(23.4, -85.2), GeoPoint(23.5, -88.0), GeoPoint(26.2, -87.8))),
        IsobarLine(992, listOf(GeoPoint(28.0, -89.5), GeoPoint(27.0, -83.5), GeoPoint(22.0, -83.8), GeoPoint(21.8, -89.8), GeoPoint(28.0, -89.5))),
        IsobarLine(1008, listOf(GeoPoint(30.0, -92.5), GeoPoint(28.8, -81.0), GeoPoint(20.0, -82.0), GeoPoint(19.5, -92.5), GeoPoint(30.0, -92.5))),
        
        // Isobars around Super Typhoon Bolaven (2026) (Central Pressure 908 hPa)
        IsobarLine(950, listOf(GeoPoint(21.0, 125.0), GeoPoint(20.5, 128.5), GeoPoint(18.0, 128.5), GeoPoint(18.0, 125.0), GeoPoint(21.0, 125.0))),
        IsobarLine(980, listOf(GeoPoint(22.8, 123.0), GeoPoint(22.0, 130.5), GeoPoint(16.5, 130.5), GeoPoint(16.5, 123.0), GeoPoint(22.8, 123.0))),
        IsobarLine(1004, listOf(GeoPoint(25.0, 120.0), GeoPoint(24.0, 133.0), GeoPoint(14.0, 133.0), GeoPoint(14.0, 120.0), GeoPoint(25.0, 120.0))),

        // Isobars around North Atlantic Bomb Cyclone (974 hPa)
        IsobarLine(984, listOf(GeoPoint(57.0, -35.0), GeoPoint(56.0, -28.0), GeoPoint(51.0, -29.0), GeoPoint(51.5, -36.0), GeoPoint(57.0, -35.0))),
        IsobarLine(1000, listOf(GeoPoint(60.0, -39.0), GeoPoint(58.5, -24.0), GeoPoint(47.5, -26.0), GeoPoint(48.0, -40.0), GeoPoint(60.0, -39.0))),

        // Subtropical High Pressure Ridge (1024 hPa Bermuda / Azores High)
        IsobarLine(1024, listOf(GeoPoint(32.0, -55.0), GeoPoint(34.0, -45.0), GeoPoint(35.0, -35.0), GeoPoint(33.0, -25.0), GeoPoint(30.0, -35.0), GeoPoint(32.0, -55.0)))
    )

    // Deep Ocean Bathymetric Trenches & Ridges (with depth in meters)
    data class BathymetryTrench(
        val name: String,
        val depthMeters: Int,
        val points: List<GeoPoint>
    )

    val bathymetryTrenches: List<BathymetryTrench> = listOf(
        BathymetryTrench("MARIANA TRENCH", -10994, listOf(
            GeoPoint(11.3, 142.2), GeoPoint(13.5, 144.5), GeoPoint(16.0, 146.8), GeoPoint(19.0, 147.5), GeoPoint(22.0, 146.0)
        )),
        BathymetryTrench("PUERTO RICO TRENCH", -8376, listOf(
            GeoPoint(19.5, -67.5), GeoPoint(19.8, -65.2), GeoPoint(19.5, -63.0), GeoPoint(19.0, -61.0)
        )),
        BathymetryTrench("MID-ATLANTIC RIDGE", -4500, listOf(
            GeoPoint(65.0, -18.0), GeoPoint(52.0, -30.0), GeoPoint(38.0, -32.0),
            GeoPoint(25.0, -45.0), GeoPoint(15.0, -44.0), GeoPoint(0.0, -25.0),
            GeoPoint(-15.0, -13.0), GeoPoint(-30.0, -14.0), GeoPoint(-45.0, -15.0)
        )),
        BathymetryTrench("SUNDA TRENCH", -7450, listOf(
            GeoPoint(6.0, 93.0), GeoPoint(2.0, 96.0), GeoPoint(-3.0, 100.0), GeoPoint(-8.0, 107.0), GeoPoint(-10.0, 115.0)
        )),
        BathymetryTrench("PERU-CHILE TRENCH", -8065, listOf(
            GeoPoint(-5.0, -81.5), GeoPoint(-12.0, -78.0), GeoPoint(-23.0, -71.5), GeoPoint(-33.0, -72.5), GeoPoint(-42.0, -75.0)
        )),
        BathymetryTrench("JAPAN TRENCH", -8410, listOf(
            GeoPoint(40.5, 144.0), GeoPoint(38.0, 143.5), GeoPoint(35.5, 142.5), GeoPoint(32.5, 141.0)
        ))
    )

    // High-Altitude Upper Troposphere Jet Streams (FL320 - FL390)
    data class JetStream(
        val id: String,
        val name: String,
        val flightLevel: String,
        val coreSpeedKts: Int,
        val points: List<GeoPoint>
    )

    val jetStreams: List<JetStream> = listOf(
        JetStream(
            id = "JET-PACIFIC-POLAR",
            name = "PACIFIC POLAR JET",
            flightLevel = "FL340",
            coreSpeedKts = 185,
            points = listOf(
                GeoPoint(32.0, 120.0), GeoPoint(34.0, 130.0), GeoPoint(36.0, 145.0),
                GeoPoint(39.0, 160.0), GeoPoint(43.0, 178.0), GeoPoint(45.0, -165.0),
                GeoPoint(43.0, -145.0), GeoPoint(41.0, -130.0), GeoPoint(44.0, -120.0)
            )
        ),
        JetStream(
            id = "JET-ATLANTIC-CORE",
            name = "NORTH ATLANTIC JET",
            flightLevel = "FL360",
            coreSpeedKts = 165,
            points = listOf(
                GeoPoint(36.0, -85.0), GeoPoint(38.0, -75.0), GeoPoint(42.0, -65.0),
                GeoPoint(47.0, -50.0), GeoPoint(51.0, -35.0), GeoPoint(54.0, -20.0),
                GeoPoint(56.0, -5.0), GeoPoint(55.0, 10.0)
            )
        ),
        JetStream(
            id = "JET-SUBTROPICAL-ASIA",
            name = "SUBTROPICAL JET",
            flightLevel = "FL390",
            coreSpeedKts = 145,
            points = listOf(
                GeoPoint(25.0, 70.0), GeoPoint(27.0, 85.0), GeoPoint(28.0, 100.0),
                GeoPoint(28.5, 115.0), GeoPoint(29.0, 130.0), GeoPoint(27.5, 145.0)
            )
        ),
        JetStream(
            id = "JET-SOUTHERN-CIRCUMPOLAR",
            name = "SOUTHERN POLAR JET",
            flightLevel = "FL320",
            coreSpeedKts = 175,
            points = listOf(
                GeoPoint(-48.0, -120.0), GeoPoint(-50.0, -80.0), GeoPoint(-49.0, -40.0),
                GeoPoint(-47.0, 0.0), GeoPoint(-49.0, 40.0), GeoPoint(-51.0, 80.0),
                GeoPoint(-48.0, 120.0), GeoPoint(-50.0, 160.0)
            )
        )
    )

    // Synoptic Surface Frontal Systems
    enum class FrontType {
        COLD, WARM, STATIONARY, OCCLUDED
    }

    data class AtmosphericFront(
        val id: String,
        val name: String,
        val type: FrontType,
        val speedKts: Int,
        val points: List<GeoPoint>
    )

    val atmosphericFronts: List<AtmosphericFront> = listOf(
        AtmosphericFront(
            id = "FRONT-COLD-ATLANTIC",
            name = "POLAR COLD FRONT",
            type = FrontType.COLD,
            speedKts = 28,
            points = listOf(
                GeoPoint(53.0, -32.0), GeoPoint(48.0, -38.0), GeoPoint(42.0, -45.0),
                GeoPoint(36.0, -52.0), GeoPoint(31.0, -60.0), GeoPoint(27.0, -70.0)
            )
        ),
        AtmosphericFront(
            id = "FRONT-WARM-GULFSTREAM",
            name = "GULF STREAM WARM FRONT",
            type = FrontType.WARM,
            speedKts = 18,
            points = listOf(
                GeoPoint(31.0, -76.0), GeoPoint(35.0, -72.0), GeoPoint(39.0, -67.0),
                GeoPoint(43.0, -60.0), GeoPoint(46.0, -52.0)
            )
        ),
        AtmosphericFront(
            id = "FRONT-STATIONARY-PACIFIC",
            name = "MEIYU/BAIU QUASI-STATIONARY",
            type = FrontType.STATIONARY,
            speedKts = 6,
            points = listOf(
                GeoPoint(24.0, 118.0), GeoPoint(26.0, 125.0), GeoPoint(28.0, 132.0),
                GeoPoint(30.0, 140.0), GeoPoint(31.0, 150.0)
            )
        ),
        AtmosphericFront(
            id = "FRONT-OCCLUDED-LABRADOR",
            name = "LABRADOR OCCLUDED SECTOR",
            type = FrontType.OCCLUDED,
            speedKts = 24,
            points = listOf(
                GeoPoint(56.0, -55.0), GeoPoint(58.0, -48.0), GeoPoint(56.0, -40.0),
                GeoPoint(53.0, -34.0)
            )
        )
    )

    // Coastal & Island Ground Doppler / NEXRAD Radar Stations
    data class DopplerRadarStation(
        val callsign: String,
        val name: String,
        val location: GeoPoint,
        val maxRangeKm: Int = 460,
        val innerRangeKm: Int = 230,
        val band: String = "S-Band 2.8 GHz"
    )

    val dopplerStations: List<DopplerRadarStation> = listOf(
        DopplerRadarStation("KAMX", "Miami NEXRAD", GeoPoint(25.61, -80.41)),
        DopplerRadarStation("KBYX", "Key West NEXRAD", GeoPoint(24.55, -81.70)),
        DopplerRadarStation("KTBW", "Tampa Bay NEXRAD", GeoPoint(27.70, -82.40)),
        DopplerRadarStation("KLIX", "New Orleans NEXRAD", GeoPoint(30.34, -89.82)),
        DopplerRadarStation("KMHX", "Cape Hatteras NEXRAD", GeoPoint(34.78, -76.88)),
        DopplerRadarStation("TJSJ", "San Juan NEXRAD", GeoPoint(18.11, -66.08)),
        DopplerRadarStation("RODN", "Kadena Doppler (Okinawa)", GeoPoint(26.35, 127.76)),
        DopplerRadarStation("RCDC", "Taipei WSR-88D", GeoPoint(25.07, 121.22)),
        DopplerRadarStation("PGUA", "Guam Andersen AFB", GeoPoint(13.58, 144.92)),
        DopplerRadarStation("PHKI", "Kauai Doppler (Hawaii)", GeoPoint(21.97, -159.45)),
        DopplerRadarStation("WIII", "Jakarta Soekarno-Hatta (BMKG)", GeoPoint(-6.13, 106.65), band = "C-Band Polarimetric"),
        DopplerRadarStation("WADD", "Bali Ngurah Rai Doppler", GeoPoint(-8.75, 115.17), band = "C-Band Doppler"),
        DopplerRadarStation("WARR", "Surabaya Juanda Doppler", GeoPoint(-7.38, 112.79), band = "C-Band Doppler"),
        DopplerRadarStation("WAAA", "Makassar Hasanuddin Doppler", GeoPoint(-5.06, 119.55), band = "C-Band Doppler")
    )

    // High-Altitude Transoceanic Corridors & Waypoints
    data class NavWaypoint(
        val code: String,
        val name: String,
        val location: GeoPoint
    )

    data class NavCorridor(
        val id: String,
        val name: String,
        val waypoints: List<GeoPoint>
    )

    val navWaypoints: List<NavWaypoint> = listOf(
        NavWaypoint("GOMEX", "Gulf of Mexico Fix", GeoPoint(25.0, -88.0)),
        NavWaypoint("BERMUDA-FIX", "Bermuda Entry Gate", GeoPoint(32.3, -64.7)),
        NavWaypoint("AZORES-INT", "Azores Oceanic Intercept", GeoPoint(38.5, -28.5)),
        NavWaypoint("MIYAKO", "Miyako Strait Corridor", GeoPoint(24.8, 125.3)),
        NavWaypoint("LUZON-FIX", "Luzon Strait Gateway", GeoPoint(20.5, 121.5)),
        NavWaypoint("MIDWAY-REP", "Midway Trans-Pac Fix", GeoPoint(28.2, -177.3)),
        NavWaypoint("MALACCA-GATE", "Strait of Malacca Entry", GeoPoint(2.5, 101.5)),
        NavWaypoint("SUNDA-FIX", "Sunda Strait Maritime Fix", GeoPoint(-5.9, 105.8)),
        NavWaypoint("LOMBOK-STR", "Lombok Strait Deep Passage", GeoPoint(-8.5, 115.8)),
        NavWaypoint("MAKASSAR-ST", "Makassar Strait Transit Gate", GeoPoint(-1.5, 118.5))
    )

    val navCorridors: List<NavCorridor> = listOf(
        NavCorridor(
            "NAT-ALPHA",
            "NORTH ATLANTIC TRACK A",
            listOf(
                GeoPoint(53.0, -10.0), GeoPoint(54.0, -20.0), GeoPoint(55.0, -30.0),
                GeoPoint(55.0, -40.0), GeoPoint(54.0, -50.0), GeoPoint(50.0, -60.0)
            )
        ),
        NavCorridor(
            "PAC-TRAC1",
            "TRANS-PACIFIC COMPOSITE",
            listOf(
                GeoPoint(34.0, 140.0), GeoPoint(38.0, 155.0), GeoPoint(43.0, 175.0),
                GeoPoint(45.0, -170.0), GeoPoint(43.0, -150.0), GeoPoint(38.0, -125.0)
            )
        ),
        NavCorridor(
            "CARIB-EXPRESS",
            "CARIBBEAN FEEDER ARTERY",
            listOf(
                GeoPoint(26.0, -80.0), GeoPoint(23.0, -75.0), GeoPoint(20.0, -70.0),
                GeoPoint(18.0, -66.0), GeoPoint(14.0, -61.0)
            )
        ),
        NavCorridor(
            "INDONESIA-TRUNK",
            "INDONESIAN ARCHIPELAGO FLIGHT TRUNK",
            listOf(
                GeoPoint(3.6, 98.7), GeoPoint(1.3, 103.8), GeoPoint(-6.2, 106.8),
                GeoPoint(-7.3, 112.8), GeoPoint(-8.7, 115.2), GeoPoint(-10.2, 123.6)
            )
        )
    )
}

