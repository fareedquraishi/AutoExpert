package com.autoexpert.app.service;

import com.autoexpert.app.data.local.dao.MessageDao;
import com.autoexpert.app.util.SessionManager;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AutoExpertFirebaseService_MembersInjector implements MembersInjector<AutoExpertFirebaseService> {
  private final Provider<SessionManager> sessionManagerProvider;

  private final Provider<MessageDao> messageDaoProvider;

  public AutoExpertFirebaseService_MembersInjector(Provider<SessionManager> sessionManagerProvider,
      Provider<MessageDao> messageDaoProvider) {
    this.sessionManagerProvider = sessionManagerProvider;
    this.messageDaoProvider = messageDaoProvider;
  }

  public static MembersInjector<AutoExpertFirebaseService> create(
      Provider<SessionManager> sessionManagerProvider, Provider<MessageDao> messageDaoProvider) {
    return new AutoExpertFirebaseService_MembersInjector(sessionManagerProvider, messageDaoProvider);
  }

  @Override
  public void injectMembers(AutoExpertFirebaseService instance) {
    injectSessionManager(instance, sessionManagerProvider.get());
    injectMessageDao(instance, messageDaoProvider.get());
  }

  @InjectedFieldSignature("com.autoexpert.app.service.AutoExpertFirebaseService.sessionManager")
  public static void injectSessionManager(AutoExpertFirebaseService instance,
      SessionManager sessionManager) {
    instance.sessionManager = sessionManager;
  }

  @InjectedFieldSignature("com.autoexpert.app.service.AutoExpertFirebaseService.messageDao")
  public static void injectMessageDao(AutoExpertFirebaseService instance, MessageDao messageDao) {
    instance.messageDao = messageDao;
  }
}
