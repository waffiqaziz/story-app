package com.dicoding.storyapp.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.data.room.RemoteKeys
import com.dicoding.storyapp.data.room.StoryDatabase

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
  private val database: StoryDatabase,
  private val apiService: ApiService,
  private val token: String
) : RemoteMediator<Int, ListStoryItem>() {
  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, ListStoryItem>
  ): MediatorResult {

    val page = when (loadType) {
      LoadType.REFRESH -> {
        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
        remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
      }
      LoadType.PREPEND -> {
        val remoteKeys = getRemoteKeyForFirstItem(state)
        val prevKey = remoteKeys?.prevKey
          ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
        prevKey
      }
      LoadType.APPEND -> {
        val remoteKeys = getRemoteKeyForLastItem(state)
        val nextKey = remoteKeys?.nextKey
          ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
        nextKey
      }
    }

    return try {
      val responseData =
        apiService.getAllStories("Bearer $token", page, state.config.pageSize).listStory
      val endOfPaginationReached = responseData.isEmpty()

      database.withTransaction {
        if (loadType == LoadType.REFRESH) {
          database.remoteKeysDao().deleteRemoteKeys()
          database.storyDao().deleteAll()
        }

        val prevKey = if (page == 1) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1

        val keys = responseData.map {
          RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
        }

        database.remoteKeysDao().insertAll(keys)
        database.storyDao().insertStory(responseData)
      }

      MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    } catch (exception: Exception) {
      MediatorResult.Error(exception)
    }
  }

  private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
    return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
      database.remoteKeysDao().getRemoteKeysId(it.id)
    }
  }

  private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
    return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
      database.remoteKeysDao().getRemoteKeysId(it.id)
    }
  }

  private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ListStoryItem>): RemoteKeys? {
    return state.anchorPosition?.let { position ->
      state.closestItemToPosition(position)?.id?.let {
        database.remoteKeysDao().getRemoteKeysId(it)
      }
    }
  }

  override suspend fun initialize(): InitializeAction {
    return InitializeAction.LAUNCH_INITIAL_REFRESH
  }

  private companion object {
    const val INITIAL_PAGE_INDEX = 1
  }
}