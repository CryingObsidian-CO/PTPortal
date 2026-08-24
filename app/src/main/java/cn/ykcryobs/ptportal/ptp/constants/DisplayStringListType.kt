package cn.ykcryobs.ptportal.ptp.constants

enum class DisplayStringListType(val code: Int, val label: String) {
    ALL_DISPLAY_LIST(0x00000000, "dsl__all"),
    BASE_LOCK_AE_LEVEL_OFFSET_EXPOSURE_VALUE_LIST(
        0x00000001, "dsl__base_lock_ae_level_offset_exposure_value"
    ),
    BASE_LOCK_INPUT_DISPLAY_LIST(0x00000002, "dsl__base_lock_input"),
    BASE_LOCK_NAME_DISPLAY_LIST(0x00000003, "dsl__base_lock_name"),
    BASE_LOCK_OUTPUT_DISPLAY_LIST(0x00000004, "dsl__base_lock_output"),
    SCENE_FILE_NAME_DISPLAY_LIST(0x00000005, "dsl__scene_file_name"),
    SHOOTING_MODE_CINEMA_COLOR_GAMUT_DISPLAY_LIST(
        0x00000006, "dsl__shooting_mode_cinema_color_gamut"
    ),
    SHOOTING_MODE_TARGET_DISPLAY_DISPLAY_LIST(
        0x00000007, "dsl__shooting_mode_target_display"
    ),
    CAMERA_GAIN_BASE_ISO_DISPLAY_LIST(0x00000008, "dsl__camera_gain_base_iso"),
    VIDEO_EI_GAIN_DISPLAY_LIST(0x00000009, "dsl__video_ei_gain"),
    BUTTON_ASSIGN_DISPLAY_LIST(0x0000000A, "dsl__button_assign"),
    BUTTON_ASSIGN_SHORT_DISPLAY_LIST(0x0000000B, "dsl__button_assign_short"),
    FTP_SERVER_NAME_DISPLAY_LIST(0x0000000C, "dsl__ftp_server_name"),
    FTP_UPLOAD_DIRECTORY_DISPLAY_LIST(0x0000000D, "dsl__ftp_upload_directory"),
    FTP_JOB_STATUS_DISPLAY_LIST(0x0000000E, "dsl__ftp_job_status"),
    EXPOSURE_INDEX_PRESET_1_DISPLAY_LIST(0x0000000F, "dsl__exposure_index_preset_1"),
    MOVIE_TRANSFER_EXTENSION_VALUE_INFORMATION_DISPLAY_LIST(
        0x00000010, "dsl__movie_transfer_extension_value_info"
    ),
    MOVIE_TRANSFER_VIDEO_CODEC_VALUE_INFORMATION_DISPLAY_LIST(
        0x00000011, "dsl__movie_transfer_video_codec_value_info"
    ),
    MOVIE_TRANSFER_FRAME_RATE_INFORMATION_DISPLAY_LIST(
        0x00000012, "dsl__movie_transfer_frame_rate_info"
    ),
    CREATIVE_LOOK_IMAGE_STYLE_DISPLAY_LIST(
        0x00000013, "dsl__creative_look_image_style"
    ),
    IPTC_META_DATA_DISPLAY_LIST(0x00000014, "dsl__iptc_metadata"),
    SUBJECT_RECOGNITION_AF_DISPLAY_LIST(0x00000015, "dsl__subject_recognition_af"),
    BASE_LOCK_META_RECORD_SUPPORT_DISPLAY_LIST(
        0x00000016, "dsl__base_lock_meta_record_support"
    ),
    TARGET_STREAMING_DESTINATION_SELECT_DISPLAY_LIST(
        0x00000017, "dsl__target_streaming_destination_select"
    ),
    CAMERA_BUTTON_FUNCTION_CAPABILITY_DISPLAY_LIST(
        0x00000018, "dsl__camera_button_function_capability"
    ),
    CAMERA_LEVER_FUNCTION_CAPABILITY_DISPLAY_LIST(
        0x00000019, "dsl__camera_lever_function_capability"
    ),
    CAMERA_DIAL_FUNCTION_CAPABILITY_DISPLAY_LIST(
        0x0000001A, "dsl__camera_dial_function_capability"
    ),
    CUSTOM_GRID_LINE_FILE_NAME_DISPLAY_LIST(
        0x0000001B, "dsl__custom_grid_line_file_name"
    );

    override fun toString(): String = "${label}(0x%08X)".format(code)

    companion object {
        fun fromCode(code: Int): DisplayStringListType? {
            return entries.find { it.code == code }
        }
    }
}
