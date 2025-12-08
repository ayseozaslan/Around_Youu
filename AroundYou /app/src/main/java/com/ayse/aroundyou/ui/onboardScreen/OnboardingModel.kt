package com.ayse.aroundyou.ui.onboardScreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ayse.aroundyou.R

sealed class OnboardingModel (
    @DrawableRes val image:Int,
    @StringRes val title: Int, //@StringRes id tutar
    @StringRes val description: Int
   ){
    data object FirstPage : OnboardingModel(
        image = R.drawable.onboard1,
        title = R.string.onboarding_title_first,
        description = R.string.onboarding_desc_first
    )
     data object  SecondPage: OnboardingModel(
         image = R.drawable.onboard2,
         title = R.string.onboarding_title_second,
         description = R.string.onboarding_desc_second
     )
     data object ThirdPage: OnboardingModel(
         image = R.drawable.onboard3,
         title = R.string.onboarding_title_third,
         description =R.string.onboarding_desc_third
     )
     data object FourPage: OnboardingModel(
         image = R.drawable.onboard4,
         title = R.string.onboarding_title_four,
         description= R.string.onboarding_desc_four
     )
     data object FivePage:OnboardingModel(
         image = R.drawable.onboard5,
         title = R.string.onboarding_title_five,
         description = R.string.onboarding_desc_five
     )
}