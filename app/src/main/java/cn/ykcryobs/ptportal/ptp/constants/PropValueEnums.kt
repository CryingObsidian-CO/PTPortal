package cn.ykcryobs.ptportal.ptp.constants

import cn.ykcryobs.ptportal.ptp.model.EnumEntry

/**
 * 白平衡枚举
 * 对应属性码 0x5005 (White Balance)
 */
enum class WhiteBalance(override val code: Long, override val label: String) : EnumEntry {
    MANUAL(0x0001, "wb_manual"),
    AWB(0x0002, "wb_awb"),
    ONE_PUSH_AUTOMATIC(0x0003, "wb_one_push_automatic"),
    DAYLIGHT(0x0004, "wb_daylight"),
    FLUORESCENT(0x0005, "wb_fluorescent"),
    TUNGSTEN(0x0006, "wb_tungsten"),
    FLASH(0x0007, "wb_flash"),
    FLUOR_WARM_WHITE_M1(0x8001, "wb_fluor_warm_white_m1"),
    FLUOR_COOL_WHITE_0(0x8002, "wb_fluor_cool_white_0"),
    FLUOR_DAY_WHITE_P1(0x8003, "wb_fluor_day_white_p1"),
    FLUOR_DAYLIGHT_WHITE_P2(0x8004, "wb_fluor_daylight_white_p2"),
    CLOUDY(0x8010, "wb_cloudy"),
    SHADE(0x8011, "wb_shade"),
    C_TEMP(0x8012, "wb_c_temp"),
    CUSTOM_1(0x8020, "wb_custom_1"),
    CUSTOM_2(0x8021, "wb_custom_2"),
    CUSTOM_3(0x8022, "wb_custom_3"),
    CUSTOM(0x8023, "wb_custom"),
    UNDERWATER_AUTO(0x8030, "wb_underwater_auto")

}

/**
 * 光圈大小枚举
 * 对应属性码 0x5007 (F-Number)
 */
enum class FNumber(override val code: Long, override val label: String) : EnumEntry {
    F1_0(0x0064, "aperture_f1_0"),                  // 1
    F1_1(0x006E, "aperture_f1_1"),                  // 1.1
    F1_2(0x0078, "aperture_f1_2"),                  // 1.2
    F1_3(0x0082, "aperture_f1_3"),                  // 1.3
    F1_4(0x008C, "aperture_f1_4"),                  // 1.4
    F1_6(0x00A0, "aperture_f1_6"),                  // 1.6
    F1_7(0x00AA, "aperture_f1_7"),                  // 1.7
    F1_8(0x00B4, "aperture_f1_8"),                  // 1.8
    F2_0(0x00C8, "aperture_f2_0"),                  // 2
    F2_2(0x00DC, "aperture_f2_2"),                  // 2.2
    F2_4(0x00F0, "aperture_f2_4"),                  // 2.4
    F2_5(0x00FA, "aperture_f2_5"),                  // 2.5
    F2_8(0x0118, "aperture_f2_8"),                  // 2.8
    F3_1(0x0136, "aperture_f3_1"),                  // 3.1
    F3_2(0x0140, "aperture_f3_2"),                  // 3.2
    F3_4(0x0154, "aperture_f3_4"),                  // 3.4
    F3_5(0x015E, "aperture_f3_5"),                  // 3.5
    F3_7(0x0172, "aperture_f3_7"),                  // 3.7
    F4_0(0x0190, "aperture_f4_0"),                  // 4
    F4_4(0x01B8, "aperture_f4_4"),                  // 4.4
    F4_5(0x01C2, "aperture_f4_5"),                  // 4.5
    F4_8(0x01E0, "aperture_f4_8"),                  // 4.8
    F5_0(0x01F4, "aperture_f5_0"),                  // 5
    F5_2(0x0208, "aperture_f5_2"),                  // 5.2
    F5_6(0x0230, "aperture_f5_6"),                  // 5.6
    F6_2(0x026C, "aperture_f6_2"),                  // 6.2
    F6_3(0x0276, "aperture_f6_3"),                  // 6.3
    F6_7(0x029E, "aperture_f6_7"),                  // 6.7
    F6_8(0x02A8, "aperture_f6_8"),                  // 6.8
    F7_1(0x02C6, "aperture_f7_1"),                  // 7.1
    F7_3(0x02DA, "aperture_f7_3"),                  // 7.3
    F8_0(0x0320, "aperture_f8_0"),                  // 8
    F8_7(0x0366, "aperture_f8_7"),                  // 8.7
    F9_0(0x0384, "aperture_f9_0"),                  // 9
    F9_5(0x03B6, "aperture_f9_5"),                  // 9.5
    F9_6(0x03C0, "aperture_f9_6"),                  // 9.6
    F10_0(0x03E8, "aperture_f10_0"),                // 10
    F11_0(0x044C, "aperture_f11_0"),                // 11
    F13_0(0x0514, "aperture_f13_0"),                // 13
    F14_0(0x0578, "aperture_f14_0"),                // 14
    F16_0(0x0640, "aperture_f16_0"),                // 16
    F18_0(0x0708, "aperture_f18_0"),                // 18
    F19_0(0x076C, "aperture_f19_0"),                // 19
    F20_0(0x07D0, "aperture_f20_0"),                // 20
    F22_0(0x0898, "aperture_f22_0"),                // 22
    F25_0(0x09C4, "aperture_f25_0"),                // 25
    F27_0(0x0A8C, "aperture_f27_0"),                // 27
    F29_0(0x0B54, "aperture_f29_0"),                // 29
    F32_0(0x0C80, "aperture_f32_0"),                // 32
    F36_0(0x0E10, "aperture_f36_0"),                // 36
    F38_0(0x0ED8, "aperture_f38_0"),                // 38
    F40_0(0x0FA0, "aperture_f40_0"),                // 40
    F45_0(0x1194, "aperture_f45_0"),                // 45
    F51_0(0x13EC, "aperture_f51_0"),                // 51
    F54_0(0x1518, "aperture_f54_0"),                // 54
    F57_0(0x1644, "aperture_f57_0"),                // 57
    F64_0(0x1900, "aperture_f64_0"),                // 64
    F72_0(0x1C20, "aperture_f72_0"),                // 72
    F76_0(0x1DB0, "aperture_f76_0"),                // 76
    F81_0(0x1FA4, "aperture_f81_0"),                // 81
    F90_0(0x2328, "aperture_f90_0"),                // 90
    IRIS_CLOSE(0xFFFD, "aperture_iris_close"),      // Iris Close
    DUMMY_DASH(0xFFFE, "aperture_dummy_dash"),      // --
    NOTHING_TO_DISPLAY(0xFFFF, "aperture_nothing"); // nothing to display
}

/**
 * 对焦模式枚举
 * 对应属性码 0x500A (Focus Mode)
 */
enum class FocusMode(override val code: Long, override val label: String) : EnumEntry {
    MANUAL_MF(0x0001, "fm_manual_mf"),               // Manual(MF)
    AF_S(0x0002, "fm_af_s"),                         // Automatic(AF_S)
    AUTOMATIC_MACRO(0x0003, "fm_auto_macro"),        // Automatic Macro(close‑up)
    AF_C(0x8004, "fm_af_c"),                         // Continuous AF(AF_C)
    AF_A(0x8005, "fm_af_a"),                        // Auto(AF_A)
    DMF(0x8006, "fm_dmf"),                           // Direct Manual Focus(DMF)
    MF_R(0x8007, "fm_mf_r"),                         // Manual Focus Reverse(MF_R)
    AF_D(0x8008, "fm_af_d"),                         // (AF‑D)
    PF(0x8009, "fm_pf");                             // Preset Focus(PF)
}

/**
 * 测光模式枚举
 * 对应属性码 0x500B (Metering Mode)
 */
enum class MeteringMode(override val code: Long, override val label: String) : EnumEntry {
    AVERAGE(0x0001, "metering_average"),                     // Average
    CENTER_WEIGHTED_AVERAGE(0x0002, "metering_center_weighted_average"), // Center‑weighted‑average
    MULTI_SPOT(0x0003, "metering_multi_spot"),              // Multi‑spot
    CENTER_SPOT(0x0004, "metering_center_spot"),            // Center‑spot
    MULTI(0x8001, "metering_multi"),                        // Multi
    CENTER_WEIGHTED(0x8002, "metering_center_weighted"),    // Center‑weighted
    ENTIRE_SCREEN_AVG(0x8003, "metering_entire_screen_avg"),// Entire Screen Avg.
    SPOT_STANDARD(0x8004, "metering_spot_standard"),        // Spot : Standard
    SPOT_LARGE(0x8005, "metering_spot_large"),              // Spot : Large
    HIGHLIGHT(0x8006, "metering_highlight"),                // Highlight
    STANDARD(0x8011, "metering_standard"),                  // Standard
    BACKLIGHT(0x8012, "metering_backlight"),                 // Backlight
    SPOTLIGHT(0x8013, "metering_spotlight");                // Spotlight
}

/**
 * 闪光模式枚举
 * 对应属性码 0x500C (Metering Mode)
 */
enum class FlashMode(override val code: Long, override val label: String) : EnumEntry {
    AUTO_FLASH(0x0001, "fl_auto"), // Auto fl
    FLASH_OFF(0x0002, "fl_off"), // Flash off
    FILL_FLASH(0x0003, "fl_fill"), // Fill flash
    RED_EYE_AUTO(0x0004, "fl_red_eye_auto"), // Red eye auto
    RED_EYE_FILL(0x0005, "fl_red_eye_fill"), // Red eye fill
    EXTERNAL_SYNC(0x0006, "fl_external_sync"), // External Sync
    SLOW_SYNC(0x8001, "fl_slow_sync"), // Slow Sync
    REAR_SYNC(0x8003, "fl_rear_sync"), // Rear Sync
    WIRELESS(0x8004, "fl_wireless"), // Wireless
    HSS_AUTO(0x8021, "fl_hss_auto"), // HSS auto
    HSS_FILL(0x8022, "fl_hss_fill"), // HSS fill
    HSS_WL(0x8024, "fl_hss_wl"), // HSS WL
    SLOW_SYNC_RED_EYE_ON(0x8031, "fl_slow_sync_re_on"),// Slow Sync Red Eye On
    SLOW_SYNC_RED_EYE_OFF(0x8032, "fl_slow_sync_re_off"),// Slow Sync Red Eye Off
    SLOW_SYNC_WIRELESS(0x8041, "fl_slow_sync_wl"), // Slow Sync Wireless
    REAR_SYNC_WIRELESS(0x8042, "fl_rear_sync_wl"); // Rear Sync Wireless
}


/**
 * 曝光模式枚举
 * 对应属性码 0x500E (Exposure Mode)
 *
 * NOTE: 修改后需要等待 500ms 保证相机完成更改
 */
enum class ExposureMode(override val code: Long, override val label: String) : EnumEntry {
    MANUAL_M(0x00000001, "cm_m"), // Manual (M)
    AUTOMATIC_P(0x00010002, "cm_p"), // Automatic (P)
    APERTURE_PRIORITY_A(0x00020003, "cm_a"), // Aperture Priority (A)
    SHUTTER_PRIORITY_S(0x00030004, "cm_s"), // Shutter Priority (S)
    PROGRAM_CREATIVE_DEPTH(
        0x00000005, "cm_program_creative_depth"
    ), // Program Creative (Greater Depth of Field)
    PROGRAM_ACTION_FASTER_SHUTTER(
        0x00000006, "cm_program_action_fast_shutter"
    ), // Program Action (Faster Shutter Speed)
    PORTRAIT(0x00000007, "cm_portrait"), // Portrait
    AUTO(0x00048000, "cm_auto"), // Auto
    AUTO_PLUS(0x00048001, "cm_auto_plus"), // Auto+
    P_A(0x00000808, "cm_p_a"), // P_A
    P_S(0x00000809, "cm_p_s"), // P_S
    SPORTS_ACTION(0x00058011, "cm_sports_action"), // Sports Action
    SUNSET(0x00058012, "cm_sunset"), // Sunset
    NIGHT_SCENE(0x00058013, "cm_night_scene"), // Night Scene
    LANDSCAPE(0x00058014, "cm_landscape"), // Landscape
    MACRO(0x00058015, "cm_macro"), // Macro
    HANDHELD_TWILIGHT(0x00058016, "cm_handheld_twilight"), // Hand‑held Twilight
    NIGHT_PORTRAIT(0x00058017, "cm_night_portrait"), // Night Portrait
    ANTI_MOTION_BLUR(0x00058018, "cm_anti_motion_blur"), // Anti Motion Blur
    PET(0x00058019, "cm_pet"), // Pet
    GOURMET(0x0005801A, "cm_gourmet"), // Gourmet
    FIREWORKS(0x0005801B, "cm_fireworks"), // Fireworks
    HIGH_SENSITIVITY(0x0005801C, "cm_high_sensitivity"), // High Sensitivity
    MEMORY_RECALL_MR(0x00000820, "cm_memory_recall_mr"), // Memory Recall (MR)
    CONTINUOUS_PRIORITY_AE(
        0x00000830, "cm_continuous_priority_ae"
    ), // Continuous Priority AE
    TELE_ZOOM_CONT_AE_8PICS(
        0x000008031, "cm_tele_zoom_cont_ae_8"
    ), // Tele‑Zoom Continuous Priority AE 8pics
    TELE_ZOOM_CONT_AE_10PICS(
        0x000008032, "cm_tele_zoom_cont_ae_10"
    ), // Tele‑Zoom Continuous Priority AE 10pics
    CONTINUOUS_PRIORITY_AE_12PICS(
        0x000008033, "cm_continuous_priority_ae_12"
    ), // Continuous Priority AE12pics
    D3_SWEEP_PANORAMA(0x000068040, "cm_3d_sweep_panorama"), // 3D Sweep Panorama Shooting
    SWEEP_PANORAMA(0x000068041, "cm_sweep_panorama"), // Sweep Panorama Shooting
    MOVIE_RECORD_P(0x000078050, "cm_movie_p"), // Movie Recording (P)
    MOVIE_RECORD_A(0x000078051, "cm_movie_a"), // Movie Recording (A)
    MOVIE_RECORD_S(0x000078052, "cm_movie_s"), // Movie Recording (S)
    MOVIE_RECORD_M(0x000078053, "cm_movie_m"), // Movie Recording (M)
    MOVIE_RECORD_AUTO(0x000078054, "cm_movie_auto"), // Movie Recording (Auto)
    MOVIE_SQ_MOTION_P(0x000098059, "cm_movie_sq_p"), // Movie Recording (S&Q Motion (P))
    MOVIE_SQ_MOTION_A(0x00009805A, "cm_movie_sq_a"), // Movie Recording (S&Q Motion (A))
    MOVIE_SQ_MOTION_S(0x00009805B, "cm_movie_sq_s"), // Movie Recording (S&Q Motion (S))
    MOVIE_SQ_MOTION_M(0x00009805C, "cm_movie_sq_m"), // Movie Recording (S&Q Motion (M))
    MOVIE_SQ_MOTION_AUTO(0x00009805D, "cm_movie_sq_auto"), // Movie Recording (S&Q Motion (Auto))
    FLASH_OFF(0x000008060, "cm_flash_off"), // Flash Off
    PICTURE_EFFECT(0x000008070, "cm_picture_effect"), // Picture Effect
    HIGH_FRAME_RATE_P(0x000088080, "cm_hfr_p"), // High Frame Rate (P)
    HIGH_FRAME_RATE_A(0x000088081, "cm_hfr_a"), // High Frame Rate (A)
    HIGH_FRAME_RATE_S(0x000088082, "cm_hfr_s"), // High Frame Rate (S)
    HIGH_FRAME_RATE_M(0x000088083, "cm_hfr_m"), // High Frame Rate (M)
    SQ_MOTION_P(0x000008084, "cm_sq_p"), // S&Q Motion (P)
    SQ_MOTION_A(0x000008085, "cm_sq_a"), // S&Q Motion (A)
    SQ_MOTION_S(0x000008086, "cm_sq_s"), // S&Q Motion (S)
    SQ_MOTION_M(0x000008087, "cm_sq_m"), // S&Q Motion (M)
    MOVIE(0x0000A8088, "cm_movie"), // Movie
    STILL(0x000A8089, "cm_still"), // Still
    F_MOVIE_SQ(0x000B808A, "cm_f_movie_sq"), // F (Movie or S&Q)
    MOVIE_F_MODE(0x000078090, "cm_movie_f_mode"), // Movie F Mode
    SQ_F_MODE(0x000098091, "cm_sq_f_mode"), // S&Q F Mode
    INTERVAL_REC_MOVIE_F(0x0000C8092, "cm_interval_rec_movie_f"), // Interval REC (Movie) F Mode
    INTERVAL_REC_MOVIE_P(0x000C8093, "cm_interval_rec_movie_p"), // Interval REC (Movie) (P)
    INTERVAL_REC_MOVIE_A(0x000C8094, "cm_interval_rec_movie_a"), // Interval REC (Movie) (A)
    INTERVAL_REC_MOVIE_S(0x000C8095, "cm_interval_rec_movie_s"), // Interval REC (Movie) (S)
    INTERVAL_REC_MOVIE_M(0x000C8096, "cm_interval_rec_movie_m"), // Interval REC (Movie) (M)
    INTERVAL_REC_MOVIE_AUTO(
        0x000C8097, "cm_interval_rec_movie_auto"
    ); // Interval REC (Movie) (Auto)
}

/**
 * 拍摄模式枚举
 * 对应属性码 0x5013 (Still Capture Mode)
 */
enum class StillCaptureMode(override val code: Long, override val label: String) : EnumEntry {
    NORMAL(0x00000001, "sc_normal"), // Normal
    CONT_SHOOTING_HI(0x00010002, "sc_cont_hi"), // Continuous Shooting Hi
    CONT_SHOOTING_HI_PLUS(0x00018010, "sc_cont_hi_plus"), // Continuous Shooting Hi+
    CONT_SHOOTING_HI_LIVE(0x00018011, "sc_cont_hi_live"), // Continuous Shooting Hi‑Live
    CONT_SHOOTING_LO(0x00018012, "sc_cont_lo"), // Continuous Shooting Lo
    CONT_SHOOTING(0x00018013, "sc_cont"), // Continuous Shooting
    CONT_SHOOTING_SPEED_PRIORITY(
        0x00018014, "sc_cont_speed_prio"
    ), // Continuous Shooting Speed Priority
    CONT_SHOOTING_MID(0x00018015, "sc_cont_mid"), // Continuous Shooting Mid
    CONT_SHOOTING_MID_LIVE(0x00018016, "sc_cont_mid_live"), // Continuous Shooting Mid‑Live
    CONT_SHOOTING_LO_LIVE(0x00018017, "sc_cont_lo_live"), // Continuous Shooting Lo‑Live
    TIMELAPSE(0x00020003, "sc_timelapse"), // Timelapse
    SELF_TIMER_5SEC(0x00038003, "sc_self_timer_5s"), // Self Timer 5 Sec.
    SELF_TIMER_10SEC(0x00038004, "sc_self_timer_10s"), // Self Timer 10 Sec.
    SELF_TIMER_2SEC(0x00038005, "sc_self_timer_2s"), // Self Timer 2 Sec.

    CONT_BRACKET_03_EV_2P_IMG_PLUS(
        0x0004C237, "sc_cont_bracket_03ev_2img_plus"
    ), // Continuous Bracket 0.3 EV 2 Img. +
    CONT_BRACKET_03_EV_2P_IMG_MINUS(
        0x0004C23F, "sc_cont_bracket_03ev_2img_minus"
    ), // Continuous Bracket 0.3 EV 2 Img. -
    CONT_BRACKET_03_EV_3P_IMG(
        0x00048337, "sc_cont_bracket_03ev_3img"
    ), // Continuous Bracket 0.3 EV 3 Img.
    CONT_BRACKET_03_EV_5P_IMG(
        0x00048537, "sc_cont_bracket_03ev_5img"
    ), // Continuous Bracket 0.3 EV 5 Img.
    CONT_BRACKET_03_EV_7P_IMG(
        0x00048737, "sc_cont_bracket_03ev_7img"
    ), // Continuous Bracket 0.3 EV 7 Img.
    CONT_BRACKET_03_EV_9P_IMG(
        0x00048937, "sc_cont_bracket_03ev_9img"
    ), // Continuous Bracket 0.3 EV 9 Img.

    CONT_BRACKET_05_EV_2P_IMG_PLUS(
        0x0004C257, "sc_cont_bracket_05ev_2img_plus"
    ), // Continuous Bracket 0.5 EV 2 Img. +
    CONT_BRACKET_05_EV_2P_IMG_MINUS(
        0x0004C25F, "sc_cont_bracket_05ev_2img_minus"
    ), // Continuous Bracket 0.5 EV 2 Img. -
    CONT_BRACKET_05_EV_3P_IMG(
        0x00048357, "sc_cont_bracket_05ev_3img"
    ), // Continuous Bracket 0.5 EV 3 Img.
    CONT_BRACKET_05_EV_5P_IMG(
        0x00048557, "sc_cont_bracket_05ev_5img"
    ), // Continuous Bracket 0.5 EV 5 Img.
    CONT_BRACKET_05_EV_7P_IMG(
        0x00048757, "sc_cont_bracket_05ev_7img"
    ), // Continuous Bracket 0.5 EV 7 Img.
    CONT_BRACKET_05_EV_9P_IMG(
        0x00048957, "sc_cont_bracket_05ev_9img"
    ), // Continuous Bracket 0.5 EV 9 Img.

    CONT_BRACKET_07_EV_2P_IMG_PLUS(
        0x0004C277, "sc_cont_bracket_07ev_2img_plus"
    ), // Continuous Bracket 0.7 EV 2 Img. +
    CONT_BRACKET_07_EV_2P_IMG_MINUS(
        0x0004C27F, "sc_cont_bracket_07ev_2img_minus"
    ), // Continuous Bracket 0.7 EV 2 Img. -
    CONT_BRACKET_07_EV_3P_IMG(
        0x00048377, "sc_cont_bracket_07ev_3img"
    ), // Continuous Bracket 0.7 EV 3 Img.
    CONT_BRACKET_07_EV_5P_IMG(
        0x00048577, "sc_cont_bracket_07ev_5img"
    ), // Continuous Bracket 0.7 EV 5 Img.
    CONT_BRACKET_07_EV_7P_IMG(
        0x00048777, "sc_cont_bracket_07ev_7img"
    ), // Continuous Bracket 0.7 EV 7 Img.
    CONT_BRACKET_07_EV_9P_IMG(
        0x00048977, "sc_cont_bracket_07ev_9img"
    ), // Continuous Bracket 0.7 EV 9 Img.

    CONT_BRACKET_10_EV_2P_IMG_PLUS(
        0x0004C211, "sc_cont_bracket_10ev_2img_plus"
    ), // Continuous Bracket 1.0 EV 2 Img. +
    CONT_BRACKET_10_EV_2P_IMG_MINUS(
        0x0004C219, "sc_cont_bracket_10ev_2img_minus"
    ), // Continuous Bracket 1.0 EV 2 Img. -
    CONT_BRACKET_10_EV_3P_IMG(
        0x00048311, "sc_cont_bracket_10ev_3img"
    ), // Continuous Bracket 1.0 EV 3 Img.
    CONT_BRACKET_10_EV_5P_IMG(
        0x00048511, "sc_cont_bracket_10ev_5img"
    ), // Continuous Bracket 1.0 EV 5 Img.
    CONT_BRACKET_10_EV_7P_IMG(
        0x00048711, "sc_cont_bracket_10ev_7img"
    ), // Continuous Bracket 1.0 EV 7 Img.
    CONT_BRACKET_10_EV_9P_IMG(
        0x00048911, "sc_cont_bracket_10ev_9img"
    ), // Continuous Bracket 1.0 EV 9 Img.

    CONT_BRACKET_13_EV_2P_IMG_PLUS(
        0x0004C241, "sc_cont_bracket_13ev_2img_plus"
    ), // Continuous Bracket 1.3 EV 2 Img. +
    CONT_BRACKET_13_EV_2P_IMG_MINUS(
        0x0004C249, "sc_cont_bracket_13ev_2img_minus"
    ), // Continuous Bracket 1.3 EV 2 Img. -
    CONT_BRACKET_13_EV_3P_IMG(
        0x00048341, "sc_cont_bracket_13ev_3img"
    ), // Continuous Bracket 1.3 EV 3 Img.
    CONT_BRACKET_13_EV_5P_IMG(
        0x00048541, "sc_cont_bracket_13ev_5img"
    ), // Continuous Bracket 1.3 EV 5 Img.
    CONT_BRACKET_13_EV_7P_IMG(
        0x00048741, "sc_cont_bracket_13ev_7img"
    ), // Continuous Bracket 1.3 EV 7 Img.

    CONT_BRACKET_15_EV_2P_IMG_PLUS(
        0x0004C261, "sc_cont_bracket_15ev_2img_plus"
    ), // Continuous Bracket 1.5 EV 2 Img. +
    CONT_BRACKET_15_EV_2P_IMG_MINUS(
        0x0004C269, "sc_cont_bracket_15ev_2img_minus"
    ), // Continuous Bracket 1.5 EV 2 Img. -
    CONT_BRACKET_15_EV_3P_IMG(
        0x00048361, "sc_cont_bracket_15ev_3img"
    ), // Continuous Bracket 1.5 EV 3 Img.
    CONT_BRACKET_15_EV_5P_IMG(
        0x00048561, "sc_cont_bracket_15ev_5img"
    ), // Continuous Bracket 1.5 EV 5 Img.
    CONT_BRACKET_15_EV_7P_IMG(
        0x00048761, "sc_cont_bracket_15ev_7img"
    ), // Continuous Bracket 1.5 EV 7 Img.

    CONT_BRACKET_17_EV_2P_IMG_PLUS(
        0x0004C281, "sc_cont_bracket_17ev_2img_plus"
    ), // Continuous Bracket 1.7 EV 2 Img. +
    CONT_BRACKET_17_EV_2P_IMG_MINUS(
        0x0004C289, "sc_cont_bracket_17ev_2img_minus"
    ), // Continuous Bracket 1.7 EV 2 Img. -
    CONT_BRACKET_17_EV_3P_IMG(
        0x00048381, "sc_cont_bracket_17ev_3img"
    ), // Continuous Bracket 1.7 EV 3 Img.
    CONT_BRACKET_17_EV_5P_IMG(
        0x00048581, "sc_cont_bracket_17ev_5img"
    ), // Continuous Bracket 1.7 EV 5 Img.
    CONT_BRACKET_17_EV_7P_IMG(
        0x00048781, "sc_cont_bracket_17ev_7img"
    ), // Continuous Bracket 1.7 EV 7 Img.

    CONT_BRACKET_20_EV_2P_IMG_PLUS(
        0x0004C221, "sc_cont_bracket_20ev_2img_plus"
    ), // Continuous Bracket 2.0 EV 2 Img. +
    CONT_BRACKET_20_EV_2P_IMG_MINUS(
        0x0004C229, "sc_cont_bracket_20ev_2img_minus"
    ), // Continuous Bracket 2.0 EV 2 Img. -
    CONT_BRACKET_20_EV_3P_IMG(
        0x00048321, "sc_cont_bracket_20ev_3img"
    ), // Continuous Bracket 2.0 EV 3 Img.
    CONT_BRACKET_20_EV_5P_IMG(
        0x00048521, "sc_cont_bracket_20ev_5img"
    ), // Continuous Bracket 2.0 EV 5 Img.
    CONT_BRACKET_20_EV_7P_IMG(
        0x00048721, "sc_cont_bracket_20ev_7img"
    ), // Continuous Bracket 2.0 EV 7 Img.

    CONT_BRACKET_23_EV_2P_IMG_PLUS(
        0x0004C251, "sc_cont_bracket_23ev_2img_plus"
    ), // Continuous Bracket 2.3 EV 2 Img. +
    CONT_BRACKET_23_EV_2P_IMG_MINUS(
        0x0004C259, "sc_cont_bracket_23ev_2img_minus"
    ), // Continuous Bracket 2.3 EV 2 Img. -
    CONT_BRACKET_23_EV_3P_IMG(
        0x00048351, "sc_cont_bracket_23ev_3img"
    ), // Continuous Bracket 2.3 EV 3 Img.
    CONT_BRACKET_23_EV_5P_IMG(
        0x00048551, "sc_cont_bracket_23ev_5img"
    ), // Continuous Bracket 2.3 EV 5 Img.

    CONT_BRACKET_25_EV_2P_IMG_PLUS(
        0x0004C271, "sc_cont_bracket_25ev_2img_plus"
    ), // Continuous Bracket 2.5 EV 2 Img. +
    CONT_BRACKET_25_EV_2P_IMG_MINUS(
        0x0004C279, "sc_cont_bracket_25ev_2img_minus"
    ), // Continuous Bracket 2.5 EV 2 Img. -
    CONT_BRACKET_25_EV_3P_IMG(
        0x00048371, "sc_cont_bracket_25ev_3img"
    ), // Continuous Bracket 2.5 EV 3 Img.
    CONT_BRACKET_25_EV_5P_IMG(
        0x00048571, "sc_cont_bracket_25ev_5img"
    ), // Continuous Bracket 2.5 EV 5 Img.

    CONT_BRACKET_27_EV_2P_IMG_PLUS(
        0x0004C291, "sc_cont_bracket_27ev_2img_plus"
    ), // Continuous Bracket 2.7 EV 2 Img. +
    CONT_BRACKET_27_EV_2P_IMG_MINUS(
        0x0004C299, "sc_cont_bracket_27ev_2img_minus"
    ), // Continuous Bracket 2.7 EV 2 Img. -
    CONT_BRACKET_27_EV_3P_IMG(
        0x00048391, "sc_cont_bracket_27ev_3img"
    ), // Continuous Bracket 2.7 EV 3 Img.
    CONT_BRACKET_27_EV_5P_IMG(
        0x00048591, "sc_cont_bracket_27ev_5img"
    ), // Continuous Bracket 2.7 EV 5 Img.

    CONT_BRACKET_30_EV_2P_IMG_PLUS(
        0x0004C231, "sc_cont_bracket_30ev_2img_plus"
    ), // Continuous Bracket 3.0 EV 2 Img. +
    CONT_BRACKET_30_EV_2P_IMG_MINUS(
        0x0004C239, "sc_cont_bracket_30ev_2img_minus"
    ), // Continuous Bracket 3.0 EV 2 Img. -
    CONT_BRACKET_30_EV_3P_IMG(
        0x00048331, "sc_cont_bracket_30ev_3img"
    ), // Continuous Bracket 3.0 EV 3 Img.
    CONT_BRACKET_30_EV_5P_IMG(
        0x00048531, "sc_cont_bracket_30ev_5img"
    ), // Continuous Bracket 3.0 EV 5 Img.

    SINGLE_BRACKET_03_EV_2P_IMG_PLUS(
        0x0005C236, "sc_single_bracket_03ev_2img_plus"
    ), // Single Bracket 0.3 EV 2 Img. +
    SINGLE_BRACKET_03_EV_2P_IMG_MINUS(
        0x0005C23E, "sc_single_bracket_03ev_2img_minus"
    ), // Single Bracket 0.3 EV 2 Img. -
    SINGLE_BRACKET_03_EV_3P_IMG(
        0x00058336, "sc_single_bracket_03ev_3img"
    ), // Single Bracket 0.3 EV 3 Img.
    SINGLE_BRACKET_03_EV_5P_IMG(
        0x00058536, "sc_single_bracket_03ev_5img"
    ), // Single Bracket 0.3 EV 5 Img.
    SINGLE_BRACKET_03_EV_7P_IMG(
        0x00058736, "sc_single_bracket_03ev_7img"
    ), // Single Bracket 0.3 EV 7 Img.
    SINGLE_BRACKET_03_EV_9P_IMG(
        0x00058936, "sc_single_bracket_03ev_9img"
    ), // Single Bracket 0.3 EV 9 Img.

    SINGLE_BRACKET_05_EV_2P_IMG_PLUS(
        0x0005C256, "sc_single_bracket_05ev_2img_plus"
    ), // Single Bracket 0.5 EV 2 Img. +
    SINGLE_BRACKET_05_EV_2P_IMG_MINUS(
        0x0005C25E, "sc_single_bracket_05ev_2img_minus"
    ), // Single Bracket 0.5 EV 2 Img. -
    SINGLE_BRACKET_05_EV_3P_IMG(
        0x00058356, "sc_single_bracket_05ev_3img"
    ), // Single Bracket 0.5 EV 3 Img.
    SINGLE_BRACKET_05_EV_5P_IMG(
        0x00058556, "sc_single_bracket_05ev_5img"
    ), // Single Bracket 0.5 EV 5 Img.
    SINGLE_BRACKET_05_EV_7P_IMG(
        0x00058756, "sc_single_bracket_05ev_7img"
    ), // Single Bracket 0.5 EV 7 Img.
    SINGLE_BRACKET_05_EV_9P_IMG(
        0x00058956, "sc_single_bracket_05ev_9img"
    ), // Single Bracket 0.5 EV 9 Img.

    SINGLE_BRACKET_07_EV_2P_IMG_PLUS(
        0x0005C276, "sc_single_bracket_07ev_2img_plus"
    ), // Single Bracket 0.7 EV 2 Img. +
    SINGLE_BRACKET_07_EV_2P_IMG_MINUS(
        0x0005C27E, "sc_single_bracket_07ev_2img_minus"
    ), // Single Bracket 0.7 EV 2 Img. -
    SINGLE_BRACKET_07_EV_3P_IMG(
        0x00058376, "sc_single_bracket_07ev_3img"
    ), // Single Bracket 0.7 EV 3 Img.
    SINGLE_BRACKET_07_EV_5P_IMG(
        0x00058576, "sc_single_bracket_07ev_5img"
    ), // Single Bracket 0.7 EV 5 Img.
    SINGLE_BRACKET_07_EV_7P_IMG(
        0x00058776, "sc_single_bracket_07ev_7img"
    ), // Single Bracket 0.7 EV 7 Img.
    SINGLE_BRACKET_07_EV_9P_IMG(
        0x00058976, "sc_single_bracket_07ev_9img"
    ), // Single Bracket 0.7 EV 9 Img.

    SINGLE_BRACKET_10_EV_2P_IMG_PLUS(
        0x0005C210, "sc_single_bracket_10ev_2img_plus"
    ), // Single Bracket 1.0 EV 2 Img. +
    SINGLE_BRACKET_10_EV_2P_IMG_MINUS(
        0x0005C218, "sc_single_bracket_10ev_2img_minus"
    ), // Single Bracket 1.0 EV 2 Img. -
    SINGLE_BRACKET_10_EV_3P_IMG(
        0x00058310, "sc_single_bracket_10ev_3img"
    ), // Single Bracket 1.0 EV 3 Img.
    SINGLE_BRACKET_10_EV_5P_IMG(
        0x00058510, "sc_single_bracket_10ev_5img"
    ), // Single Bracket 1.0 EV 5 Img.
    SINGLE_BRACKET_10_EV_7P_IMG(
        0x00058710, "sc_single_bracket_10ev_7img"
    ), // Single Bracket 1.0 EV 7 Img.
    SINGLE_BRACKET_10_EV_9P_IMG(
        0x00058910, "sc_single_bracket_10ev_9img"
    ), // Single Bracket 1.0 EV 9 Img.

    SINGLE_BRACKET_13_EV_2P_IMG_PLUS(
        0x0005C240, "sc_single_bracket_13ev_2img_plus"
    ), // Single Bracket 1.3 EV 2 Img. +
    SINGLE_BRACKET_13_EV_2P_IMG_MINUS(
        0x0005C248, "sc_single_bracket_13ev_2img_minus"
    ), // Single Bracket 1.3 EV 2 Img. -
    SINGLE_BRACKET_13_EV_3P_IMG(
        0x00058340, "sc_single_bracket_13ev_3img"
    ), // Single Bracket 1.3 EV 3 Img.
    SINGLE_BRACKET_13_EV_5P_IMG(
        0x00058540, "sc_single_bracket_13ev_5img"
    ), // Single Bracket 1.3 EV 5 Img.
    SINGLE_BRACKET_13_EV_7P_IMG(
        0x00058740, "sc_single_bracket_13ev_7img"
    ), // Single Bracket 1.3 EV 7 Img.

    SINGLE_BRACKET_15_EV_2P_IMG_PLUS(
        0x0005C260, "sc_single_bracket_15ev_2img_plus"
    ), // Single Bracket 1.5 EV 2 Img. +
    SINGLE_BRACKET_15_EV_2P_IMG_MINUS(
        0x0005C268, "sc_single_bracket_15ev_2img_minus"
    ), // Single Bracket 1.5 EV 2 Img. -
    SINGLE_BRACKET_15_EV_3P_IMG(
        0x00058360, "sc_single_bracket_15ev_3img"
    ), // Single Bracket 1.5 EV 3 Img.
    SINGLE_BRACKET_15_EV_5P_IMG(
        0x00058560, "sc_single_bracket_15ev_5img"
    ), // Single Bracket 1.5 EV 5 Img.
    SINGLE_BRACKET_15_EV_7P_IMG(
        0x00058760, "sc_single_bracket_15ev_7img"
    ), // Single Bracket 1.5 EV 7 Img.

    SINGLE_BRACKET_17_EV_2P_IMG_PLUS(
        0x0005C280, "sc_single_bracket_17ev_2img_plus"
    ), // Single Bracket 1.7 EV 2 Img. +
    SINGLE_BRACKET_17_EV_2P_IMG_MINUS(
        0x0005C288, "sc_single_bracket_17ev_2img_minus"
    ), // Single Bracket 1.7 EV 2 Img. -
    SINGLE_BRACKET_17_EV_3P_IMG(
        0x00058380, "sc_single_bracket_17ev_3img"
    ), // Single Bracket 1.7 EV 3 Img.
    SINGLE_BRACKET_17_EV_5P_IMG(
        0x00058580, "sc_single_bracket_17ev_5img"
    ), // Single Bracket 1.7 EV 5 Img.
    SINGLE_BRACKET_17_EV_7P_IMG(
        0x00058780, "sc_single_bracket_17ev_7img"
    ), // Single Bracket 1.7 EV 7 Img.

    SINGLE_BRACKET_20_EV_2P_IMG_PLUS(
        0x0005C220, "sc_single_bracket_20ev_2img_plus"
    ), // Single Bracket 2.0 EV 2 Img. +
    SINGLE_BRACKET_20_EV_2P_IMG_MINUS(
        0x0005C228, "sc_single_bracket_20ev_2img_minus"
    ), // Single Bracket 2.0 EV 2 Img. -
    SINGLE_BRACKET_20_EV_3P_IMG(
        0x00058320, "sc_single_bracket_20ev_3img"
    ), // Single Bracket 2.0 EV 3 Img.
    SINGLE_BRACKET_20_EV_5P_IMG(
        0x00058520, "sc_single_bracket_20ev_5img"
    ), // Single Bracket 2.0 EV 5 Img.
    SINGLE_BRACKET_20_EV_7P_IMG(
        0x00058720, "sc_single_bracket_20ev_7img"
    ), // Single Bracket 2.0 EV 7 Img.

    SINGLE_BRACKET_23_EV_2P_IMG_PLUS(
        0x0005C250, "sc_single_bracket_23ev_2img_plus"
    ), // Single Bracket 2.3 EV 2 Img. +
    SINGLE_BRACKET_23_EV_2P_IMG_MINUS(
        0x0005C258, "sc_single_bracket_23ev_2img_minus"
    ), // Single Bracket 2.3 EV 2 Img. -
    SINGLE_BRACKET_23_EV_3P_IMG(
        0x00058350, "sc_single_bracket_23ev_3img"
    ), // Single Bracket 2.3 EV 3 Img.
    SINGLE_BRACKET_23_EV_5P_IMG(
        0x00058550, "sc_single_bracket_23ev_5img"
    ), // Single Bracket 2.3 EV 5 Img.

    SINGLE_BRACKET_25_EV_2P_IMG_PLUS(
        0x0005C270, "sc_single_bracket_25ev_2img_plus"
    ), // Single Bracket 2.5 EV 2 Img. +
    SINGLE_BRACKET_25_EV_2P_IMG_MINUS(
        0x0005C278, "sc_single_bracket_25ev_2img_minus"
    ), // Single Bracket 2.5 EV 2 Img. -
    SINGLE_BRACKET_25_EV_3P_IMG(
        0x00058370, "sc_single_bracket_25ev_3img"
    ), // Single Bracket 2.5 EV 3 Img.
    SINGLE_BRACKET_25_EV_5P_IMG(
        0x00058570, "sc_single_bracket_25ev_5img"
    ), // Single Bracket 2.5 EV 5 Img.

    SINGLE_BRACKET_27_EV_2P_IMG_PLUS(
        0x0005C290, "sc_single_bracket_27ev_2img_plus"
    ), // Single Bracket 2.7 EV 2 Img. +
    SINGLE_BRACKET_27_EV_2P_IMG_MINUS(
        0x0005C298, "sc_single_bracket_27ev_2img_minus"
    ), // Single Bracket 2.7 EV 2 Img. -
    SINGLE_BRACKET_27_EV_3P_IMG(
        0x00058390, "sc_single_bracket_27ev_3img"
    ), // Single Bracket 2.7 EV 3 Img.
    SINGLE_BRACKET_27_EV_5P_IMG(
        0x00058590, "sc_single_bracket_27ev_5img"
    ), // Single Bracket 2.7 EV 5 Img.

    SINGLE_BRACKET_30_EV_2P_IMG_PLUS(
        0x0005C230, "sc_single_bracket_30ev_2img_plus"
    ), // Single Bracket 3.0 EV 2 Img. +
    SINGLE_BRACKET_30_EV_2P_IMG_MINUS(
        0x0005C238, "sc_single_bracket_30ev_2img_minus"
    ), // Single Bracket 3.0 EV 2 Img. -
    SINGLE_BRACKET_30_EV_3P_IMG(
        0x00058330, "sc_single_bracket_30ev_3img"
    ), // Single Bracket 3.0 EV 3 Img.
    SINGLE_BRACKET_30_EV_5P_IMG(
        0x00058530, "sc_single_bracket_30ev_5img"
    ), // Single Bracket 3.0 EV 5 Img.

    WB_BRACKET_LO(0x00068018, "sc_wb_bracket_lo"), // White Balance Bracket Lo
    WB_BRACKET_HI(0x00068028, "sc_wb_bracket_hi"), // White Balance Bracket Hi
    DRO_BRACKET_LO(0x00078019, "sc_dro_bracket_lo"), // DRO Bracket Lo
    DRO_BRACKET_HI(0x00078029, "sc_dro_bracket_hi"), // DRO Bracket Hi
    LPF_BRACKET(0x0007801A, "sc_lpf_bracket"), // LPF Bracket
    REMOTE_COMMANDER(0x0007800A, "sc_remote_commander"), // Remote Commander
    MIRROR_UP(0x0007800B, "sc_mirror_up"), // Mirror Up
    SELF_PORTRAIT_1P(0x00078006, "sc_self_portrait_1p"), // Self Portrait 1 Person
    SELF_PORTRAIT_2P(0x00078007, "sc_self_portrait_2p"), // Self Portrait 2 People

    CONT_SELF_TIMER_3IMG(
        0x00088008, "sc_cont_self_timer_3img"
    ), // Continuous Self Timer 3 Img.
    CONT_SELF_TIMER_5IMG(
        0x00088009, "sc_cont_self_timer_5img"
    ), // Continuous Self Timer 5 Img.
    CONT_SELF_TIMER_3IMG_5SEC(
        0x0008800C, "sc_cont_self_timer_3img_5s"
    ), // Continuous Self Timer 3 Img. 5 Sec.
    CONT_SELF_TIMER_5IMG_5SEC(
        0x0008800D, "sc_cont_self_timer_5img_5s"
    ), // Continuous Self Timer 5 Img. 5 Sec.
    CONT_SELF_TIMER_3IMG_2SEC(
        0x0008800E, "sc_cont_self_timer_3img_2s"
    ), // Continuous Self Timer 3 Img. 2 Sec.
    CONT_SELF_TIMER_5IMG_2SEC(
        0x0008800F, "sc_cont_self_timer_5img_2s"
    ), // Continuous Self Timer 5 Img. 2 Sec.

    SPOT_BURST_LO(0x00098030, "sc_spot_burst_lo"), // Spot Burst Shooting Lo
    SPOT_BURST_MID(0x00098031, "sc_spot_burst_mid"), // Spot Burst Shooting Mid
    SPOT_BURST_HI(0x00098032, "sc_spot_burst_hi"), // Spot Burst Shooting Hi

    FOCUS_BRACKET(0x000A8040, "sc_focus_bracket"); // Focus Bracket
}

/**
 * 光圈模式枚举
 * 对应属性码 0xD001 (Iris Mode)
 *
 * NOTE: 几乎只有电影机能用上的参数，绝大部分机器的光圈模式由拍摄模式决定，而不是这个参数
 */
enum class IrisMode(override val code: Long, override val label: String) : EnumEntry {
    AUTOMATIC(0x01, "im_automatic"), MANUAL(0x02, "im_manual")
}

/**
 * 电池电量指示器枚举
 * 对应属性码 0xD20E (Battery Level Indicator)
 *
 */
enum class BatteryLevel(override val code: Long, override val label: String) : EnumEntry {
    FAKE_BATTERY(0x01, "im_fake_battery"),
    UNUSABLE(0x02, "im_unusable"),
    PRE_END_BATTERY(0x03, "im_pre_end_battery"),
    BATTERY_LEVEL_1_4(0x04, "im_battery_level_1_4"),
    BATTERY_LEVEL_2_4(0x05, "im_battery_level_2_4"),
    BATTERY_LEVEL_3_4(0x06, "im_battery_level_3_4"),
    BATTERY_LEVEL_4_4(0x07, "im_battery_level_4_4"),
    BATTERY_LEVEL_1_3(0x08, "im_battery_level_1_3"),
    BATTERY_LEVEL_2_3(0x09, "im_battery_level_2_3"),
    BATTERY_LEVEL_3_3(0x0A, "im_battery_level_3_3"),
    PRE_END_BATTERY_USB_SUPPLY(0x0B, "im_pre_end_battery_usb_supply"),
    BATTERY_LEVEL_1_4_USB_SUPPLY(0x0C, "im_battery_level_1_4_usb_supply"),
    BATTERY_LEVEL_2_4_USB_SUPPLY(0x0D, "im_battery_level_2_4_usb_supply"),
    BATTERY_LEVEL_3_4_USB_SUPPLY(0x0E, "im_battery_level_3_4_usb_supply"),
    BATTERY_LEVEL_4_4_USB_SUPPLY(0x0F, "im_battery_level_4_4_usb_supply"),
    USB_BUS_POWER_SUPPLY(0x10, "im_usb_bus_power_supply"),
    BATTERY_NOT_INSTALLED(0xFF, "im_battery_not_installed");
}

/**
 * 电池电量枚举
 * 对应属性码 0xD218 (Battery Remaining)
 *
 */
enum class BatteryRemaining(override val code: Long, override val label: String) : EnumEntry {
    UNTAKEN(0xFF, "untaken")
}