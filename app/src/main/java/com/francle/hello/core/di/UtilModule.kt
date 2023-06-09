package com.francle.hello.core.di

import android.app.Application
import android.media.MediaMetadataRetriever
import com.francle.hello.core.data.download.Downloader
import com.francle.hello.core.data.download.DownloaderImpl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UtilModule {
    @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application): Player {
        return ExoPlayer.Builder(app)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideDownloader(app: Application): Downloader {
        return DownloaderImpl(app)
    }

    @Provides
    @ViewModelScoped
    fun provideRetriever(): MediaMetadataRetriever {
        return MediaMetadataRetriever()
    }
}
