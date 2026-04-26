package com.autoexpert.app.ui.messaging;

import com.autoexpert.app.data.local.dao.MessageDao;
import com.autoexpert.app.data.remote.api.SupabaseApi;
import com.autoexpert.app.util.SessionManager;
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
public final class MessagingViewModel_Factory implements Factory<MessagingViewModel> {
  private final Provider<MessageDao> messageDaoProvider;

  private final Provider<SupabaseApi> apiProvider;

  private final Provider<SessionManager> sessionProvider;

  public MessagingViewModel_Factory(Provider<MessageDao> messageDaoProvider,
      Provider<SupabaseApi> apiProvider, Provider<SessionManager> sessionProvider) {
    this.messageDaoProvider = messageDaoProvider;
    this.apiProvider = apiProvider;
    this.sessionProvider = sessionProvider;
  }

  @Override
  public MessagingViewModel get() {
    return newInstance(messageDaoProvider.get(), apiProvider.get(), sessionProvider.get());
  }

  public static MessagingViewModel_Factory create(Provider<MessageDao> messageDaoProvider,
      Provider<SupabaseApi> apiProvider, Provider<SessionManager> sessionProvider) {
    return new MessagingViewModel_Factory(messageDaoProvider, apiProvider, sessionProvider);
  }

  public static MessagingViewModel newInstance(MessageDao messageDao, SupabaseApi api,
      SessionManager session) {
    return new MessagingViewModel(messageDao, api, session);
  }
}
