package com.myapp.aries.chatapp.service

import android.app.job.JobParameters
import android.app.job.JobService
import timber.log.Timber

class NotificationJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.tag("JobService").d("onStartJob")
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Timber.tag("JobService").d("onStopJob")
        return false
    }

}
