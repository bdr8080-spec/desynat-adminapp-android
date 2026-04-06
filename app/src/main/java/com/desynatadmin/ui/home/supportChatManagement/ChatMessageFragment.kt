package com.desynatadmin.ui.home.supportChatManagement

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.ChatMessageAdapter
import com.desynatadmin.adapter.UploadMsgImageAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentChatMessageBinding
import com.desynatadmin.ui.home.supportChatManagement.response.ChatMessage
import com.desynatadmin.ui.home.supportChatManagement.response.SendMessageRequest
import com.desynatadmin.ui.home.supportChatManagement.translation.ChatLanguageDetector
import com.desynatadmin.ui.home.supportChatManagement.translation.TranslateRequest
import com.desynatadmin.ui.home.supportChatManagement.translation.TranslatorRetrofit
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.mediaPicker.ImagePicker
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant

@AndroidEntryPoint
class ChatMessageFragment : BaseFragment() {
    private var binding: FragmentChatMessageBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isImageObserver = true
    private var type = 0
    private var chatList = ArrayList<ChatMessage>()
    private var chatMessageAdapter: ChatMessageAdapter?=null

    private var uploadMsgImageAdapter: UploadMsgImageAdapter? = null
    private var uploadImageList = ArrayList<UploadImageType>()


    private var uniqueId = ""

    var isFirstMessageLoaded = false

    private var imageUri: Uri? = null

    private lateinit var imagePicker: ImagePicker
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentChatMessageBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.chats)

            chatMessageAdapter = ChatMessageAdapter(requireActivity(),chatList,object :
                ChatMessageAdapter.ChatClick{
                override fun onChatClick(position: Int) {

                }

            })
            rvChat.adapter = chatMessageAdapter

            listenMessages(
                uniqueId = uniqueId,
                onMessageAdded = { message ->
                    if (!isFirstMessageLoaded) {
                        pbLoader.isVisible = false
                        isFirstMessageLoaded = true
                    }

                    chatList.add(message)
                    chatMessageAdapter?.notifyItemInserted(chatList.size - 1)
                    rvChat.scrollToPosition(chatList.size - 1)

                    if (message.by.toSafeString() != "user" && message.read.toSafeInt() == 0 && message.id.toSafeString() != null && message.id.toSafeString() != "") {
                        markMessageRead(uniqueId, message.id.toSafeString())
                    }
                },
                onMessageUpdated = { updatedMessage ->
                    Log.e("update", "initView: ${updatedMessage}")

                    val index =
                        chatList.indexOfFirst { it.id.toSafeString() == updatedMessage.id.toSafeString() }
                    Log.e("update", "id: ${index}")
                    Log.e("update", "id: ${chatList[index]}")
                    if (index != -1) {
                        chatList[index] = updatedMessage
                        chatMessageAdapter?.notifyItemChanged(index)
                    }
                }
            )

            uploadImageList.clear()
            uploadMsgImageAdapter = UploadMsgImageAdapter(
                requireActivity(),
                uploadImageList,
                object : UploadMsgImageAdapter.Click {
                    override fun onClick(position: Int) {
                        uploadImageList.removeAt(position)
                        uploadMsgImageAdapter?.notifyDataSetChanged()
                    }

                })
            rvUploadImg.adapter = uploadMsgImageAdapter

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    imageUri = it
                    uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSend.setOnClickListener {

                val textMessage = etMessage.text?.trim().toString()
                val hasText = textMessage.isNotEmpty()
                val hasImages = uploadImageList.isNotEmpty()

                if (!hasText && !hasImages) {
                    showToast("Please enter message")
                    return@setOnClickListener
                }

                if (hasText) {
                    val isArabic = ChatLanguageDetector.isArabic(textMessage)

                    if (isArabic) {
                        // Arabic typed → translate to English
                        callGoogleTranslatorApi(
                            input = textMessage,
                            source = "ar",
                            target = "en"
                        )
                    } else {
                        // English typed → translate to Arabic
                        callGoogleTranslatorApi(
                            input = textMessage,
                            source = "en",
                            target = "ar"
                        )
                    }
                }

                if (hasImages) {
                    uploadImageList.forEach { image ->
                        sendMessage(
                            uniqueId = uniqueId,
                            message = image.imageUrl,
                            messageAr = image.imageUrl,
                            type = "1"
                        )
                    }
                    uploadImageList.clear()
                    uploadMsgImageAdapter?.notifyDataSetChanged()
                }
            }

            ivAttachment.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver || isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
                isImageObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    imageUri?.let { it1 -> UploadImageType(it1, response.data.url) }
                        ?.let { it2 -> uploadImageList.add(it2) }
                    uploadMsgImageAdapter?.notifyDataSetChanged()

                    imageUri = null
                }
                isImageObserver = false
            }
        }
    }

    fun uploadFile(image: File) {
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    fun markMessageRead(uniqueId: String, messageId: String) {
        val map = HashMap<String, Any>()
        map["read"] = 1

        database.child("chats")
            .child(uniqueId)
            .child(messageId)
            .updateChildren(map)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun callGoogleTranslatorApi(input: String, source: String, target: String) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        lifecycleScope.launch {
            try {
                val response = TranslatorRetrofit.api.googleTranslator(
                    apiKey = requireActivity().getString(R.string.google_translator_api_key),
                    request = TranslateRequest(
                        q = input,
                        source = source,
                        target = target
                    )
                )
                ProgressDialogUtils.getInstance().hideProgress()
                if (response.isSuccessful) {
                    val translatedText =
                        response.body()
                            ?.data
                            ?.translations
                            ?.firstOrNull()
                            ?.translatedText

                    if (source == "ar") {
                        sendMessage(
                            uniqueId = uniqueId,
                            message = translatedText.toString(),   // English
                            messageAr = input,          // Arabic
                            type = "0"
                        )
                    } else {
                        sendMessage(
                            uniqueId = uniqueId,
                            message = input,            // English
                            messageAr = translatedText.toString(), // Arabic
                            type = "0"
                        )
                    }

                    binding?.etMessage?.setText("")
                    Log.e("chat_response", translatedText ?: "No translation")
                } else {
                    Log.e("chat_response", "API Error: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
                Log.e("VISION_ERROR", e.message ?: "Error")
            }
        }
    }

    private val database =
        FirebaseDatabase.getInstance().reference

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(uniqueId: String, message: String, messageAr: String?=null, type: String) {
        val ref = database.child("chats").child(uniqueId).push()
        val messageId = ref.key ?: return

        val msg = SendMessageRequest(
            id = messageId,
            by = "user",
            message = message,
            messageAr = messageAr,
            type = type,
            unique_id = uniqueId,
            times = Instant.now().toString(),
            user_id = SharedPreference.get(requireActivity()).getUser()?.id?.toLong() ?: 0L,
            read = 0
        )

        ref.setValue(msg)
    }


    fun listenMessages(
        uniqueId: String,
        onMessageAdded: (ChatMessage) -> Unit,
        onMessageUpdated: (ChatMessage) -> Unit
    ) {
        database.child("chats")
            .child(uniqueId)
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, prev: String?) {
                    snapshot.getValue(ChatMessage::class.java)
                        ?.let { onMessageAdded(it) }
                }

                override fun onChildChanged(snapshot: DataSnapshot, prev: String?) {
                    snapshot.getValue(ChatMessage::class.java)
                        ?.let { onMessageUpdated(it) }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, prev: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }


}

fun Any?.toSafeString(): String {
    return when (this) {
        is String -> this
        is Long -> this.toString()
        is Int -> this.toString()
        is Double -> this.toString()
        else -> ""
    }
}

fun Any?.toSafeInt(): Int {
    return when (this) {
        is Int -> this
        is Long -> this.toInt()
        is String -> this.toIntOrNull() ?: 0
        else -> 0
    }
}

fun Any?.toSafeLong(): Long {
    return when (this) {
        is Long -> this
        is Int -> this.toLong()
        is String -> this.toLongOrNull() ?: 0L
        else -> 0L
    }
}

data class UploadImageType(
    var image:Uri,
    var imageUrl:String
)