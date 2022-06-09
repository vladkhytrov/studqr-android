package com.vladkhytrov.studpresence.data

enum class MsgType(val value: String) {

    LECTURE_START("lecture_start"),
    LECTURE_STOP("lecture_stop"),
    QR_REFRESH("qr_refresh"),
    QR_NEW("qr_new"),
    QR_SCANNED("qr_scanned"),
    QR_SCAN_SUCCESS("qr_scan_success"),
    QR_SCAN_ERROR("qr_scan_error")

}