package upload

data class ApkDTO(
    val version_code: Long? = null,
    val version_name: String? = null,
    val upgrade_content: String? = null,
    val pkg_url: String? = null,
    val is_force_upgrade: Boolean? = null,
    val app_platform: Int? = null,
    val hash_256: String? = null,
    val apk_size: Long? = null,
//    val create_time: Long? = null,
)