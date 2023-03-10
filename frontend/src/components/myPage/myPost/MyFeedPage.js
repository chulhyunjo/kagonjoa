import { useState } from "react"
import { Menu } from "semantic-ui-react"

import MyPosts from "./MyPosts"
import MyComments from "./MyComments"

const MyFeedPage = () => {
  const [activeItem, setActiveItem] = useState('myposts')

  const handleItemClick = (e, { dir }) => {
    setActiveItem(dir)
  }

  return (
    <>
      <Menu color="blue" widths={2} style={{mairginTop:"3rem"}}>
        <Menu.Item
          name="내 게시글"
          dir="myposts"
          active={activeItem === "myposts"}
          onClick={handleItemClick}
        />
        <Menu.Item
          name="내 댓글"
          dir="mycomments"
          active={activeItem === "mycomments"}
          onClick={handleItemClick}
        />
      </Menu>

      {activeItem === "myposts" && <MyPosts />}
      {activeItem === "mycomments" && <MyComments />}
      {/* {activeItem === "mylikes" && <MyLikes />} */}
    </>
  )
}

export default MyFeedPage
