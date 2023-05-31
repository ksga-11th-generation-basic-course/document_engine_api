<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://kshrdalumni.herokuapp.com/swagger-ui/index.html">
    <img src="https://kshrdalumni.herokuapp.com/images/lqfulgatq109nudqosmegdphoto_2022-05-29_21-15-40.jpg" alt="Logo" width="180" height="180">
  </a>
</p>
<h3 align="center">DocEngine</h3>

  <p align="center">
    DocEngine is a platform that gives users the ability to create documents and manage those documents within the team 
.
    <br />
    <br />
    <a href="https://kshrdalumni.herokuapp.com/swagger-ui/index.html">View Demo</a>
  </p>

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#installation">Installation</a>
    </li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
<li><a href="#what-we-have-done">What we have done</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

### Built With

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [Postgresql](https://www.postgresql.org/)


<!-- GETTING STARTED -->

## Getting Started

### Installation
### NOTE: JDK 19 required for all installation methods

## Usage

## Steps to test API

### [AuthenticationController](http://localhost:8080/swagger-ui/index.html#/user-authentication-controller)
- Step 01 -> [Register](http://localhost:8080/swagger-ui/index.html#/user-authentication-controller/register)
- Step 02 -> [Verify](http://localhost:8080/swagger-ui/index.html#/user-authentication-controller/verify)
- Step 03 -> [Login](http://localhost:8080/swagger-ui/index.html#/user-authentication-controller/login)

### [UserController](http://localhost:8080/swagger-ui/index.html#/user-controller)
- Step 04 -> [Close Account](http://localhost:8080/swagger-ui/index.html#/user-controller/closeAccount)
**Note:**  The user can close their account.
- Step 05 -> [get current user](http://localhost:8080/swagger-ui/index.html#/user-controller/getCurrentUser)
-> [get all user ](http://localhost:8080/swagger-ui/index.html#/user-controller/getAllUser)
- Step 06 -> [update user profile image](http://localhost:8080/swagger-ui/index.html#/user-controller/changeProfileImage)
- Step 07 -> [change user username](http://localhost:8080/swagger-ui/index.html#/user-controller/changeUsername)
  -> [change user password](http://localhost:8080/swagger-ui/index.html#/user-controller/changePassword) -> [delete user profile image](http://localhost:8080/swagger-ui/index.html#/user-controller/deleteProfileImage)


### [WorkspaceController](http://localhost:8080/swagger-ui/index.html#/workspace-controller)
- Step 08 -> [create workspace](http://localhost:8080/swagger-ui/index.html#/workspace-controller/createWorkspace)
- Step 09 -> [join workspace](http://localhost:8080/swagger-ui/index.html#/workspace-controller/joinWorkspace)
- Step 10 -> [get total of document](http://localhost:8080/swagger-ui/index.html#/workspace-controller/getTotalOfDocument)
- Step 11 -> [get all workspace](http://localhost:8080/swagger-ui/index.html#/workspace-controller/getAllWorkspace)
- Step 12 -> [search workspace by workspace name](http://localhost:8080/swagger-ui/index.html#/workspace-controller/searchWorkspace)
- Step 13 -> [filter workspace](http://localhost:8080/swagger-ui/index.html#/workspace-controller/filterWorkspace)
- Step 14 -> [remove workspace](http://localhost:8080/swagger-ui/index.html#/workspace-controller/removeWorkspace)
- Step 15 -> [remove member from workspace](http://localhost:8080/swagger-ui/index.html#/workspace-controller/removeMemberFromWorkspace)
- Step 16 -> [leave workspace](http://localhost:8080/swagger-ui/index.html#/workspace-controller/leaveWorkspace)
- Step 17 -> [delete workspace image](http://localhost:8080/swagger-ui/index.html#/workspace-controller/deleteWorkspaceImage)


### [DocumentController](http://localhost:8080/swagger-ui/index.html#/document-controller)
- Step 18 -> [ create document](http://localhost:8080/swagger-ui/index.html#/document-controller/createDocument)
- Step 19 -> [ duplicate document](http://localhost:8080/swagger-ui/index.html#/document-controller/duplicateDocument)
- Step 20 -> [ view document](http://localhost:8080/swagger-ui/index.html#/document-controller/viewDocument)
- Step 21 -> [ get document in each workspace](http://localhost:8080/swagger-ui/index.html#/document-controller/getDocumentInEachWorkspace)
- Step 22- > [ search document by tag name](http://localhost:8080/swagger-ui/index.html#/document-controller/searchDocumentByTagName)
- Step 23 -> [ search document by many tag name](http://localhost:8080/swagger-ui/index.html#/document-controller/searchDocumentByTagName_1)
- Step 24 -> [ edit document](http://localhost:8080/swagger-ui/index.html#/document-controller/editDocument)
- Step 25 -> [ set accessibility](http://localhost:8080/swagger-ui/index.html#/document-controller/setAccessibility)
- Step 26 -> [ current editing document](http://localhost:8080/swagger-ui/index.html#/document-controller/editDocument_1)
- Step 27 -> [ delete document](http://localhost:8080/swagger-ui/index.html#/document-controller/deleteDocument)

### [BlockController](http://localhost:8080/swagger-ui/index.html#/block-controller)
- Step 28 -> [ create block](http://localhost:8080/swagger-ui/index.html#/block-controller/createBlock)
- Step 29 -> [ get block for each document](http://localhost:8080/swagger-ui/index.html#/block-controller/getBlockForEachDocument)
- Step 30 -> [ edit block](http://localhost:8080/swagger-ui/index.html#/block-controller/editBlock)
- Step 31 -> [ delete block](http://localhost:8080/swagger-ui/index.html#/block-controller/deleteBlock)

### [TagController](http://localhost:8080/swagger-ui/index.html#/tag-controller)
- Step 32 -> [get all tag](http://localhost:8080/swagger-ui/index.html#/tag-controller/getAllTag)
- Step 33 -> [create tag](http://localhost:8080/swagger-ui/index.html#/tag-controller/createTag)
- Step 34 -> [get tag in each workspace](http://localhost:8080/swagger-ui/index.html#/tag-controller/getTagInEachWorkspace)
- Step 35 -> [edit tag](http://localhost:8080/swagger-ui/index.html#/tag-controller/editTag)
- Step 36 -> [delete tag](http://localhost:8080/swagger-ui/index.html#/tag-controller/deleteTag)

### [HistoryController](http://localhost:8080/swagger-ui/index.html#/history-controller)
- Step 37 -> [ get history by historyId](http://localhost:8080/swagger-ui/index.html#/history-controller/getHistoryByHistoryId)
- Step 38 -> [ get history in each document](http://localhost:8080/swagger-ui/index.html#/history-controller/getHistoryInEachDocument)
- Step 39 -> [ restore document](http://localhost:8080/swagger-ui/index.html#/history-controller/restoreDocument)
- Step 40 -> [ remove history](http://localhost:8080/swagger-ui/index.html#/history-controller/removeHistory)

### [ContactController](http://localhost:8080/swagger-ui/index.html#/contact-controller)

- Step 18 -> [contact us ](http://localhost:8080/swagger-ui/index.html#/contact-controller/contactUs)


_For more examples, please refer to
the [service Demo](http://localhost:8080/swagger-ui/index.html#)


## What we have done

- [AuthenticationController](http://localhost:8080/swagger-ui/index.html#/user-authentication-controller)
- [UserController](http://localhost:8080/swagger-ui/index.html#/user-controller)
- [WorkspaceController](http://localhost:8080/swagger-ui/index.html#/workspace-controller)
- [DocumentController](http://localhost:8080/swagger-ui/index.html#/document-controller)
- [BlockController](http://localhost:8080/swagger-ui/index.html#/block-controller)
- [TagController](http://localhost:8080/swagger-ui/index.html#/tag-controller)
- [HistoryController](http://localhost:8080/swagger-ui/index.html#/history-controller)
- [ContactController](http://localhost:8080/swagger-ui/index.html#/contact-controller)





