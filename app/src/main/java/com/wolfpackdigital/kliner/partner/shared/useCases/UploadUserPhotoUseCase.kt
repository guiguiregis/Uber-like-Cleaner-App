package com.wolfpackdigital.kliner.partner.shared.useCases

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.annotation.Keep
import coil.ImageLoader
import coil.request.ImageRequest
import com.wolfpackdigital.kliner.partner.data.repo.MainRepoI
import com.wolfpackdigital.kliner.partner.shared.base.BaseUseCase
import com.wolfpackdigital.kliner.partner.shared.base.Result
import com.wolfpackdigital.kliner.partner.shared.utils.buildImageBodyPart
import com.wolfpackdigital.kliner.partner.shared.utils.extensions.getParsedError
import okhttp3.MultipartBody

const val FILENAME = "photo"
const val ID_CARD_FRONT = "front"
const val ID_CARD_BACK = "back"

class UploadUserPhotoUseCase(
    private val context: Context,
    private val repo: MainRepoI
) : BaseUseCase<UploadUserPhotoRequest, Unit>() {
    override suspend fun run(params: UploadUserPhotoRequest) = try {
        val loader = ImageLoader(context)
        val multipartMap = mutableMapOf<String, MultipartBody.Part>()
        for (entry in params.uriMap) {
            val imageRequest = ImageRequest.Builder(context).data(entry.value).build()
            val bitmap = (loader.execute(imageRequest).drawable as BitmapDrawable).bitmap
            val multipart = context.buildImageBodyPart(
                entry.key,
                entry.key,
                bitmap
            )
            multipartMap.put(entry.key, multipart)
        }

        val result = when (params.type) {
            PhotoTypes.ID_CARD -> repo.uploadPhotos(params.type, multipartMap)
            else -> repo.uploadPhoto(params.type, multipartMap[FILENAME])
        }

        Result.Success(result)
    } catch (throwable: Throwable) {
        Result.Error(throwable.getParsedError())
    }
}

@Keep
enum class PhotoTypes {
    ID_CARD,
    PASSPORT,
    RESIDENCE_PERMIT,
    PROFILE_PHOTO,
    NOVA_CERTIFICATE,
    INSURANCE_CERTIFICATE,
    ALL
}

@Keep
data class UploadUserPhotoRequest(
    val type: PhotoTypes,
    val uriMap: Map<String, Uri>
)
