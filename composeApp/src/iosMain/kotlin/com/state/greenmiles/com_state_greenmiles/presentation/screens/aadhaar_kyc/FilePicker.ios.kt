package com.state.greenmiles.com_state_greenmiles.presentation.screens.aadhaar_kyc

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject
import platform.posix.memcpy

actual class FilePicker {

    @OptIn(ExperimentalForeignApi::class)
    actual fun pickFile(onFilePicked: (ByteArray) -> Unit) {

        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("public.data"),
            inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
        )

        picker.delegate = object : NSObject(),
            UIDocumentPickerDelegateProtocol {

            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {

                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                val data = url?.let { NSData.dataWithContentsOfURL(it) }

                if (data != null) {
                    val bytes = ByteArray(data.length.toInt())
                    memScoped {
                        memcpy(
                            bytes.refTo(0),
                            data.bytes,
                            data.length
                        )
                    }
                    onFilePicked(bytes)
                }
            }
        }

        val rootVC = UIApplication.sharedApplication
            .keyWindow!!
            .rootViewController!!

        rootVC.presentViewController(
            picker,
            animated = true,
            completion = null
        )
    }
}