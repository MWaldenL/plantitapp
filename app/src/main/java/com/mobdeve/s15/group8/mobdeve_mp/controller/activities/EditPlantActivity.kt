package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.google.firebase.Timestamp
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.AddPlantTasksAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.ImageUploadCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.ImageUploadService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.TaskService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Error
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditPlantActivity :
    AppCompatActivity(),
    ImageUploadCallback,
    AddPlantTasksAdapter.OnTaskDeletedListener
{
    private lateinit var rvTasks: RecyclerView
    private lateinit var ivPlant: ImageView
    private lateinit var ibtnAddTask: ImageButton
    private lateinit var ibtnSave: ImageButton
    private lateinit var etPlantName: EditText
    private lateinit var etPlantNickname: EditText
    private lateinit var mPhotoFilename: String
    private lateinit var mPlantData: Plant
    private lateinit var mPlantDataEditable: Plant

    private val mNewTasks: ArrayList<Task> = arrayListOf()
    private val mDeletedTasks: ArrayList<String> = arrayListOf()

    private val addTaskLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val action = result.data?.getCharSequenceExtra(
                    getString(R.string.ADD_TASK_ACTION)).toString()
                val startDate = result.data?.getLongExtra(
                    getString(R.string.ADD_TASK_START_DATE), 0)
                val occurrence = result.data?.getCharSequenceExtra(
                    getString(R.string.ADD_TASK_OCCURRENCE)).toString()
                val repeat = result.data?.getIntExtra(
                    getString(R.string.ADD_TASK_REPEAT), 0) as Int

                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    plantId = mPlantData.id,
                    userId = F.auth.uid!!,
                    action = action,
                    startDate = Date(startDate!!),
                    occurrence = occurrence,
                    repeat = repeat,
                    lastCompleted = DateTimeService.getLastDueDate(
                        occurrence,
                        repeat,
                        Date(startDate)
                    ).time
                )
                mNewTasks.add(newTask)
                mPlantDataEditable.tasks.add(newTask.id)
                (rvTasks.adapter as AddPlantTasksAdapter).addNewTask(newTask)
            }
        }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            // reduce image quality
            val bm = BitmapFactory.decodeFile(mPhotoFilename)
            val file = File(mPhotoFilename)
            file.createNewFile()
            val fos = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 10, fos)
            fos.close()

            val uri = Uri.fromFile(File(mPhotoFilename))
            try { // create the bitmap from uri
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                // show the image
                ivPlant.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plant)

        mInitViews()
        mBindData()
    }

    private fun mInitViews() {
        ivPlant = findViewById(R.id.iv_plant_edit)
        ibtnAddTask = findViewById(R.id.ibtn_add_task_edit)
        ibtnSave = findViewById(R.id.ibtn_save_plant_edit)
        etPlantName = findViewById(R.id.et_plant_name_edit)
        etPlantNickname = findViewById(R.id.et_plant_nickname_edit)

        ivPlant.setOnClickListener { mOpenCamera() }
        ibtnSave.setOnClickListener { mSavePlant() }
        ibtnAddTask.setOnClickListener {
            val i = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(i)
        }

        rvTasks = findViewById(R.id.rv_tasks_edit)
        rvTasks.layoutManager = LinearLayoutManager(this)
    }

    private fun mBindData() {
        mPlantData = intent.getParcelableExtra(getString(R.string.PLANT_KEY))!!
        mPlantDataEditable = mPlantData.deepCopy()

        val (id, userId, imageUrl, filePath, name, nickname, datePurchased, death, taskIds, journal) = mPlantData
        val tasks = TaskService.findTasksByPlantId(id)

        mPhotoFilename = filePath

        if (filePath == "") {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivPlant)
        } else {
            val imgFile = File(filePath)
            val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
            ivPlant.setImageBitmap(bmp)
        }

        etPlantName.setText(name)
        etPlantNickname.setText(nickname)

        rvTasks.adapter = AddPlantTasksAdapter(this, tasks)
    }

    private fun mSavePlant() {
        mPlantDataEditable.name = etPlantName.text.toString()
        mPlantDataEditable.nickname = etPlantNickname.text.toString()
        mPlantDataEditable.filePath = mPhotoFilename

        DBService.updateDocument(
            F.plantsCollection,
            mPlantData.id,
            hashMapOf(
                "name" to mPlantDataEditable.name,
                "nickname" to mPlantDataEditable.nickname,
                "tasks" to mPlantDataEditable.tasks,
                "filePath" to mPhotoFilename
            )
        )

        for (task in mNewTasks) {
            DBService.addDocument(
                F.tasksCollection,
                task.id,
                hashMapOf(
                    "id" to task.id,
                    "plantId" to task.plantId,
                    "userId" to task.userId,
                    "action" to task.action,
                    "startDate" to Timestamp(task.startDate),
                    "repeat" to task.repeat,
                    "occurrence" to task.occurrence,
                    "lastCompleted" to Timestamp(task.lastCompleted)
                )
            )

            PlantRepository.taskList.add(task)
        }

        for (taskID in mDeletedTasks) {
            DBService.deleteDocument(
                F.tasksCollection,
                taskID
            )

            val task = TaskService.findTaskById(taskID)
            PlantRepository.taskList.remove(task)
        }

        try {
            ImageUploadService.uploadToCloud(mPhotoFilename)
        } catch (err: Error) {
            MediaManager.init(this)
        }

        val index = PlantRepository.plantList.indexOf(mPlantData)
        PlantRepository.plantList[index] = mPlantDataEditable

        mPlantData = PlantRepository.plantList[index]

        val resultIntent = Intent()
        resultIntent.putExtra(getString(R.string.PLANT_KEY), mPlantData)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun notifyTaskDeleted(task: Task) {
        mDeletedTasks.add(task.id)
        mPlantDataEditable.tasks.remove(task.id)
    }

    override fun onImageUploadSuccess(imageUrl: String) {
        DBService.updateDocument(
            F.plantsCollection,
            mPlantData.id,
            "imageUrl",
            imageUrl)
    }

    private fun mOpenCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    mCreateImageFile()
                } catch (ex: IOException) {
                    Log.e("CAM", "$ex")
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        getString(R.string.file_provider_authority),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun mCreateImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filename = "plant_${timeStamp}"
        return File.createTempFile(filename, ".jpg", storageDir).apply {
            mPhotoFilename = absolutePath
        }
    }
}