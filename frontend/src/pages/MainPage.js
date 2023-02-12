import { Fragment } from "react"
import { Grid } from "semantic-ui-react"
import { useState } from "react"
import MainPageTopBar from "../components/mainPage/MainPageTopBar"
import PostList from "../components/mainPage/PostList"
import JamSurvey from "../components/mainPage/JamSurvey"

const MainPage = ({
  isAuthenticated,
  isCafeAuth,
  isJamSurvey,
  setIsJamSurvey,
}) => {
  return (
    <Fragment>
      <Grid>
        <Grid.Column width={16}>
          <MainPageTopBar
            isAuthenticated={isAuthenticated}
            isCafeAuth={isCafeAuth}
          />
        </Grid.Column>
        <Grid.Column width={16}>
          {!isJamSurvey &&
            isAuthenticated !== undefined &&
            isCafeAuth === '1' && (
              <JamSurvey setIsJamSurvey={setIsJamSurvey} />
            )}
        </Grid.Column>
        {/* <Grid.Column width={16}>
          <PostTypeCarousel />
        </Grid.Column> */}
        <Grid.Column width={16}>
          <div>
            <PostList />
          </div>
        </Grid.Column>
      </Grid>
    </Fragment>
  )
}

export default MainPage
