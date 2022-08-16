multibranchPipelineJob('mmp-builder') {
    factory {
      workflowBranchProjectFactory {
        scriptPath('Jenkinsfile')
      }
    }
    description("MMP PR Builder")
    branchSources {
      branchSource {
        source {
          github {
            credentialsId('github-bot-token')
            repoOwner('moneypark')
            repository('mmp')
            repositoryUrl('https://github.com/moneypark/mmp')
            configuredByUrl(false)
            traits {
              cleanAfterCheckoutTrait()
              cleanBeforeCheckoutTrait()
              cloneOptionTrait {
               extension {
                  noTags(false)
                  shallow(false)
                  depth(0)
                  reference('/home/jenkins/mmp')
                  timeout(null)
                }
              }
              gitHubBranchDiscovery {
                strategyId(3)
              }
              notificationContextTrait {
                contextLabel('{{ jenkins_prefix_notification_context }}jenkins/builder')
                typeSuffix(true)
              }
            }
          }
        }
        strategy {
          namedExceptionsBranchPropertyStrategy {
            defaultProperties {
              noTriggerBranchProperty()
              triggerPRCommentBranchProperty {
                commentBody('{{ jenkins_trigger_build_pr_comment }}')
              }
            }
            namedExceptions {
              named {
                name('master')
              }
              named {
                name('mmp-patch')
              }
              named {
                name('frontend')
              }
            }
          }
        }
      }
    }

    orphanedItemStrategy {
      discardOldItems {
        daysToKeep(10)
        numToKeep(100)
      }
    }

    configure {
      def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
      traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
          strategyId(1)
      }

      traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
          strategyId(1)
          trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustEveryone')
      }
    }

}
