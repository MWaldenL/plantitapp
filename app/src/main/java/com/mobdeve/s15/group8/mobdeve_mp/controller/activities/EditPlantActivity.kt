package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.google.firebase.Timestamp
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CameraService
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.AddPlantTasksAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.ImageUploadCallback
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.ImageUploadService
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.*
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.io.File
import java.util.*

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
    private lateinit var tvErrName: TextView
    private lateinit var tvErrNickname: TextView
    private lateinit var mPhotoFilename: String
    private lateinit var mOldNickname: String
    private lateinit var mPlantData: Plant
    private lateinit var mPlantDataEditable: Plant

    private val mNewTasks: ArrayList<Task> = arrayListOf()
    private val mDeletedTasks: ArrayList<String> = arrayListOf()

    private val addTaskLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
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

    private val cameraLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = CameraService.getBitmap(mPhotoFilename, contentResolver)
            ivPlant.setImageBitmap(bitmap)
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
        tvErrName = findViewById(R.id.tv_edit_err_name)
        tvErrNickname = findViewById(R.id.tv_edit_err_nickname)
        ivPlant.setOnClickListener {
            mPhotoFilename = CameraService.launchCameraAndGetFilename(
                context=this,
                authority=getString(R.string.file_provider_authority),
                launcher=cameraLauncher)
        }
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
        mOldNickname = etPlantNickname.text.toString()

        rvTasks.adapter = AddPlantTasksAdapter(this, tasks)
    }

    private fun mCheckNickname(): Boolean {
        val currentNickname = etPlantNickname.text.toString()
        val plant = PlantService.findPlantByNickname(currentNickname)
        return plant == null || // for newly-named plants
                currentNickname == mOldNickname || // if did not change nickname
                currentNickname != plant.nickname // changed nickname and unique
    }

    private fun mCheckFields(): Boolean {
        val nameFilled = etPlantName.text.isNotEmpty()
        val nicknameUnique = mCheckNickname()
        tvErrName.visibility = if (!nameFilled) View.VISIBLE else View.GONE
        tvErrNickname.visibility = if (!nicknameUnique) View.VISIBLE else View.GONE
        return nameFilled && nicknameUnique
    }

    private fun mSavePlant() {
        if (!mCheckFields()) {
            return
        }

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
}