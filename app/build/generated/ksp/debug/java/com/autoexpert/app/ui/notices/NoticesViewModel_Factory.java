package com.autoexpert.app.ui.notices;

import com.autoexpert.app.data.local.dao.NoticeDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NoticesViewModel_Factory implements Factory<NoticesViewModel> {
  private final Provider<NoticeDao> noticeDaoProvider;

  public NoticesViewModel_Factory(Provider<NoticeDao> noticeDaoProvider) {
    this.noticeDaoProvider = noticeDaoProvider;
  }

  @Override
  public NoticesViewModel get() {
    return newInstance(noticeDaoProvider.get());
  }

  public static NoticesViewModel_Factory create(Provider<NoticeDao> noticeDaoProvider) {
    return new NoticesViewModel_Factory(noticeDaoProvider);
  }

  public static NoticesViewModel newInstance(NoticeDao noticeDao) {
    return new NoticesViewModel(noticeDao);
  }
}
