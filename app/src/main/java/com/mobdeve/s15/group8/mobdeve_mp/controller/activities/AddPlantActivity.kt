package com.mobdeve.s15.group8.mobdeve_mp.controller.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CameraService
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.AddPlantTasksAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.interfaces.ImageUploadCallback
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.ImageUploadService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import java.io.File
import java.util.*

class AddPlantActivity :
    AppCompatActivity(),
    ImageUploadCallback,
    AddPlantTasksAdapter.OnTaskDeletedListener
{
    private lateinit var tasksRV: RecyclerView
    private lateinit var ivPlant: ImageView
    private lateinit var ivAddPlant: ImageView
    private lateinit var ibtnAddTask: ImageButton
    private lateinit var ibtnSavePlant: ImageButton
    private lateinit var tvErrNickname: TextView
    private lateinit var tvErrName: TextView
    private lateinit var tvErrImage: TextView
    private lateinit var etPlantName: EditText
    private lateinit var etPlantNickname: EditText
    private lateinit var groupNoPic: ConstraintLayout
    private lateinit var mPhotoFilename: String
    private val mPlantId = UUID.randomUUID().toString()

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
                    plantId = mPlantId,
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
                NewPlantInstance.addTask(newTask)
                (tasksRV.adapter as AddPlantTasksAdapter).addNewTask(newTask)
            }
        }

    private val cameraLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) { // fetch bitmap and show image to user
            val bitmap = CameraService.getBitmap(mPhotoFilename, contentResolver)
            groupNoPic.visibility = View.GONE
            ivPlant.visibility = View.VISIBLE
            ivPlant.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)
        ImageUploadService.setOnUploadSuccessListener(this)

        // Clear NewPlantInstance
        NewPlantInstance.resetPlant()
        NewPlantInstance.resetTasks()

        groupNoPic = findViewById(R.id.group_no_pic)
        ivAddPlant = findViewById(R.id.iv_no_pic)
        ivPlant = findViewById(R.id.iv_add_plant)
        etPlantName = findViewById(R.id.et_plant_name)
        etPlantNickname = findViewById(R.id.et_plant_nickname)
        tvErrName = findViewById(R.id.tv_err_name)
        tvErrNickname = findViewById(R.id.tv_edit_err_nickname)
        tvErrImage = findViewById(R.id.tv_err_image)
        ibtnAddTask = findViewById(R.id.ibtn_add_task)
        ibtnSavePlant = findViewById(R.id.ibtn_save_plant)
        tasksRV = findViewById(R.id.rv_tasks)
        tasksRV.adapter = AddPlantTasksAdapter(this, NewPlantInstance.tasksObject)
        tasksRV.layoutManager = LinearLayoutManager(this)

        ivAddPlant.setOnClickListener {
            mPhotoFilename = CameraService.launchCameraAndGetFilename(
                context=this,
                authority=getString(R.string.file_provider_authority),
                launcher=cameraLauncher)
        }
        ivPlant.setOnClickListener {
            mPhotoFilename = CameraService.launchCameraAndGetFilename(
                context=this,
                authority=getString(R.string.file_provider_authority),
                launcher=cameraLauncher)
        }
        ibtnSavePlant.setOnClickListener { mSavePlant() }
        ibtnAddTask.setOnClickListener {
            val i = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(i)
        }
    }

    override fun notifyTaskDeleted(task: Task) {
        NewPlantInstance.removeTask(task)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(getString(R.string.SAVED_PLANT_KEY), NewPlantInstance.plant)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val plant = savedInstanceState.getSerializable(getString(R.string.SAVED_PLANT_KEY))
    }

    override fun onImageUploadSuccess(imageUrl: String) {
        DBService.updateDocument(
            collection= F.plantsCollection,
            id=mPlantId,
            field="imageUrl",
            value=imageUrl)
    }

    private fun mCheckFields(): Boolean {
        val nameFilled = etPlantName.text.isNotEmpty()
        val nicknameUnique = PlantService.findPlantByNickname(etPlantNickname.text.toString()) == null
        val imageAdded = groupNoPic.visibility == View.GONE && ivPlant.visibility == View.VISIBLE
        tvErrName.visibility = if (!nameFilled) View.VISIBLE else View.GONE
        tvErrNickname.visibility = if (!nicknameUnique) View.VISIBLE else View.GONE
        tvErrImage.visibility = if (!imageAdded) View.VISIBLE else View.GONE
        return nameFilled && nicknameUnique && imageAdded
    }

    private fun mSavePlant() {
        if (!mCheckFields()) {
            return
        }

        // Compile final map to write to firebase
        NewPlantInstance.setStaticParams(
            id=mPlantId,
            userId=F.auth.uid!!,
            name=etPlantName.text.toString(),
            nick=etPlantNickname.text.toString(),
            filePath=mPhotoFilename,
            death=false)

        // Write plant to firebase first
        DBService.addDocument(
            collection=F.plantsCollection,
            id=mPlantId,
            data=NewPlantInstance.plant)

        // Write the tasks to firebase
        for (task in NewPlantInstance.tasks) {
            // update the plantId first
            task["plantId"] = mPlantId
            DBService.addDocument(
                collection = F.tasksCollection,
                id = task["id"].toString(),
                data = task
            )
        }

        // Write to local
        for (taskObj in NewPlantInstance.tasksObject) {
            taskObj.plantId = mPlantId
            PlantRepository.taskList.add(taskObj)
        }

        // Notify the ViewAllPlantsAdapter of a dataset change
        PlantRepository.plantList.add(NewPlantInstance.plantObject)
        NewPlantInstance.notifyPlantRV()

        // Then upload to cloudinary and reset the new plant instance
        try {
            ImageUploadService.uploadToCloud(mPhotoFilename)
        } catch (err: Error) {
            MediaManager.init(this)
        }

        // Go back to MainActivity
        Intent(this@AddPlantActivity, MainActivity::class.java)
        setResult(Activity.RESULT_OK)
        finish()
    }
}