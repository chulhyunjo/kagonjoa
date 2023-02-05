import { Menu, Image } from "semantic-ui-react"
import { useHistory, useLocation } from "react-router-dom"
import { useDispatch } from "react-redux"

import { findMapCafeList } from "../../store/cafe"

import { BsFillPatchQuestionFill } from "react-icons/bs"
import { AiOutlineBell } from "react-icons/ai"

import SearchModal from "./SearchModal"

const MainPageTopBar = (props) => {
  const history = useHistory()
  const location = useLocation()
  const dispatch = useDispatch()

  const findCafeList = () => {
    dispatch(
      findMapCafeList({
        lat: sessionStorage.getItem("lat"),
        lng: sessionStorage.getItem("lng"),
        distance: 0.3,
      })
    )
  }

  const openMapHandler = () => {
    if (location.pathname === "/") {
      findCafeList()
      history.push("/map")
    }
    if (location.pathname === "/map") {
      history.push("/")
    }
  }

  const isAuthenticated = sessionStorage.getItem("cafeAuth")

  return (
    <Menu borderless fluid size="mini" style={{ border: "none" }}>
      <Menu.Item header>
        {props.isAuthenticated ? (
          <Image
            src="https://s3-alpha-sig.figma.com/img/0207/5992/b4ca9f4af86076f8b39354c7b31c0526?Expires=1676246400&Signature=Sk89L11wkpikP8yd71d2ErDCIimjqaCTKDn6WJhA7ZrNUAeCOsxKr79HaNCEnRQHSwh124lDWkJmcapf7j7BtrxUMbOvN6fC0WEotXP3-UEhC3UghL0JFVQdvK8SIaJk7VObcl8y19fIEimk4nA-oTnwV1UZ1k5GHsO4yTf7nhlPER1ntq652jYv3prrMHXreXVUFe4XG3PpNIh8n2tH8rNyWL6CcECjFmyzodWjFgUQ24Yvno8PGHzZoSo5zfAnMU0JgAaKRpboihC5tCLmQ22DF58mPrPVq7XdOeW0xA4qMC7XjXiA0atZRUSLWJp~Vaebvtppjc5oCYj9Jhg-qw__&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4"
            size="mini"
            style={{ marginInline: "5% 10%" }}
          />
        ) : (
          <BsFillPatchQuestionFill
            style={{ marginInline: "1rem" }}
            size="36"
            color="grey"
          />
        )}
        <p
          style={{
            whiteSpace: "nowrap",
            fontSize: "1.3rem",
          }}
        >
          강남구 역삼동
        </p>
      </Menu.Item>
      <Menu.Item
        icon="angle down"
        onClick={openMapHandler}
        size="large"
        style={{ marginLeft: "2%" }}
      />
      <Menu.Item position="right">
        <Menu.Item>
          <AiOutlineBell size="30" color="black" />
        </Menu.Item>
        <Menu.Item>
          <SearchModal />
        </Menu.Item>
      </Menu.Item>
    </Menu>
  )
}

export default MainPageTopBar
