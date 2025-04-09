package com.rmp.data.repository.nutrition

import com.rmp.data.ApiClient
import com.rmp.data.successOr

class NutritionRepoImpl : NutritionRepository {
    override suspend fun loadDailyStats(date: NutritionStatRequest): NutritionStatResponse? {
        val response = ApiClient.authorizedRequest<NutritionStatResponse>(
            ApiClient.Method.POST,
            "/users/stat/summary",
            date
        )

        return response.successOr(null)
    }

    override suspend fun getGeneratedMenu(date: GeneratedMenuRequest): GeneratedMenu? {
        val response = ApiClient.authorizedRequest<GeneratedMenu>(
            ApiClient.Method.POST,
            "paprika/calculate",
            date
        )

        return response.successOr(null)
    }

    override suspend fun saveGeneratedMenu(date: SaveMenuRequest): SaveMenuResponse? {
        val response = ApiClient.authorizedRequest<SaveMenuResponse>(
            ApiClient.Method.POST,
            "/users/menu",
            date
        )

        return response.successOr(null)
    }

    override suspend fun getMenu(): Menu? {
        val response = ApiClient.authorizedRequest<Menu>(
            ApiClient.Method.GET,
            "/users/menu"
        )

        return response.successOr(null)
    }

    override suspend fun switchDishCheckbox(date: SwitchDishCheckboxRequest): AddRemoveSelectResponse? {
        val response = ApiClient.authorizedRequest<AddRemoveSelectResponse>(
            ApiClient.Method.POST,
            "/users/log/dish",
            date
        )

        return response.successOr(null)
    }

    override suspend fun removeMenuItem(date: RemoveMenuItemRequest): AddRemoveSelectResponse? {
        val response = ApiClient.authorizedRequest<AddRemoveSelectResponse>(
            ApiClient.Method.DELETE,
            "/users/menu",
            date
        )

        return response.successOr(null)
    }

    override suspend fun getMenuStats(date: NutritionStatRequest): NutritionHistory? {
        val response = ApiClient.authorizedRequest<NutritionHistory>(
            ApiClient.Method.POST,
            "users/stat/menu",
            date
        )

        return response.successOr(null)
    }

    override suspend fun addMenuItem(data: AddMenuItem): MenuItemAdded? {
        val response = ApiClient.authorizedRequest<MenuItemAdded>(
            ApiClient.Method.PATCH,
            "/users/menu",
            data
        )

        return response.successOr(null)
    }

    override suspend fun addMenuItem(data: AddMenuItemFromDish): MenuItemAdded? {
        val response = ApiClient.authorizedRequest<MenuItemAdded>(
            ApiClient.Method.PATCH,
            "/users/menu",
            data
        )

        return response.successOr(null)
    }

    override suspend fun getDish(filter: FilterDto): SearchResultDto? =
        ApiClient.authorizedRequest<SearchResultDto>(ApiClient.Method.POST, "dish/find", filter).successOr(null)
}