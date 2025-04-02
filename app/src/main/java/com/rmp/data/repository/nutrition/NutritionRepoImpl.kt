package com.rmp.data.repository.nutrition

import com.rmp.data.ApiClient
import com.rmp.data.successOr

class NutritionRepoImpl : NutritionRepository {
    override suspend fun logNutrition(nutritionLog: NutritionDailyRecord): NutritionLogResponse? {
        val response = ApiClient.authorizedRequest<NutritionLogResponse>(
            ApiClient.Method.POST,
            "users/log/water",
            nutritionLog
        )
        return response.successOr(null)
    }

    override suspend fun getDailyNutritionStats(date: NutritionStatRequest): NutritionStatResponse? {
        val response = ApiClient.authorizedRequest<NutritionStatResponse>(
            ApiClient.Method.POST,
            "users/stat/water",
            date
        )

        return response.successOr(null)
    }

    override suspend fun getGeneratedMenu(date: GeneratedMenuRequest): GeneratedMenuResponse? {
        val response = ApiClient.authorizedRequest<GeneratedMenuResponse>(
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

    override suspend fun getMenu(): GetMenuResponse? {
        val response = ApiClient.authorizedRequest<GetMenuResponse>(
            ApiClient.Method.GET,
            "/users/menu"
        )

        return response.successOr(null)
    }

    override suspend fun switchDishCheckbox(date: SwitchDishCheckboxRequest): SwitchDishCheckboxResponse? {
        val response = ApiClient.authorizedRequest<SwitchDishCheckboxResponse>(
            ApiClient.Method.POST,
            "/users/log/dish",
            date
        )

        return response.successOr(null)
    }

    override suspend fun removeMenuItem(menuItemId: Int): RemoveMenuItemResponse? {
        val response = ApiClient.authorizedRequest<RemoveMenuItemResponse>(
            ApiClient.Method.DELETE,
            "/users/menu",
            menuItemId
        )

        return response.successOr(null)
    }
}